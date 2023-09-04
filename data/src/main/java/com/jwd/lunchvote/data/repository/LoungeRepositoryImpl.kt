package com.jwd.lunchvote.data.repository

import com.jwd.lunchvote.data.mapper.type.LoungeChatDataMapper
import com.jwd.lunchvote.data.model.type.MessageDataType
import com.jwd.lunchvote.data.source.local.LoungeLocalDataSource
import com.jwd.lunchvote.data.source.remote.LoungeRemoteDataSource
import com.jwd.lunchvote.data.worker.SendWorkerManager
import com.jwd.lunchvote.domain.entity.LoungeChat
import com.jwd.lunchvote.domain.entity.Member
import com.jwd.lunchvote.domain.entity.MemberStatus
import com.jwd.lunchvote.domain.repository.LoungeRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import java.util.UUID
import javax.inject.Inject

class LoungeRepositoryImpl @Inject constructor(
    private val sendWorkerManager: SendWorkerManager,
    private val loungeLocalDataSource: LoungeLocalDataSource,
    private val loungeRemoteDataSource: LoungeRemoteDataSource
): LoungeRepository {
    override suspend fun checkLoungeExist(loungeId: String): Boolean {
        return loungeRemoteDataSource.checkLoungeExist(loungeId)
    }

    override suspend fun createLounge(): String {
        val id = UUID.randomUUID().toString()
        val loungeId = loungeRemoteDataSource.createLounge()
        loungeRemoteDataSource.sendChat(id, loungeId, null, MessageDataType.CREATE)
        return loungeId
    }

    override suspend fun joinLounge(loungeId: String) {
        val id = UUID.randomUUID().toString()
        loungeRemoteDataSource.joinLounge(loungeId)
        loungeRemoteDataSource.sendChat(id, loungeId, null, MessageDataType.JOIN)
    }

    override fun getMemberList(loungeId: String): Flow<List<Member>> {
        return loungeLocalDataSource.getMemberList(loungeId)
            .filter { it.isNotEmpty() }
            .onStart { syncMemberList(loungeId) }
    }

    override fun getChatList(loungeId: String): Flow<List<LoungeChat>> {
        return loungeLocalDataSource.getChatList(loungeId)
            .map{it.map(LoungeChatDataMapper::mapToRight)}
            .filter { it.isNotEmpty() }
            .onStart { syncChatList(loungeId) }
    }

    // 일반 채팅 메시지 보내는 경우
    override suspend fun sendChat(loungeId: String, content: String) {
        val id = UUID.randomUUID().toString()
        sendWorkerManager.startSendWork(id, loungeId, content)
        return loungeLocalDataSource.insertChat(id, loungeId, content, MessageDataType.NORMAL)
    }

    override suspend fun updateReady(uid: String, loungeId: String) {
        loungeLocalDataSource.updateMemberReady(uid, loungeId)
        loungeRemoteDataSource.updateReady(uid, loungeId)
    }

    override suspend fun exitLounge(uid: String, loungeId: String) {
        val id = UUID.randomUUID().toString()
        loungeRemoteDataSource.sendChat(id, loungeId, null, MessageDataType.EXIT)
        loungeRemoteDataSource.exitLounge(uid, loungeId)
        loungeLocalDataSource.deleteAllChat(loungeId)
    }

    override fun getMemberStatus(uid: String, loungeId: String): Flow<MemberStatus>{
        return loungeRemoteDataSource.getMemberList(loungeId)
            .map {
                if (it.isEmpty()){
                    MemberStatus.EXITED
                }
                else if (it.find { member -> member.uid == uid } == null) {
                    // 추방 당한 것
                    MemberStatus.EXILED
                }
                else {
                    MemberStatus.NORMAL
                }
            }
    }


    private suspend fun syncMemberList(loungeId: String){
        loungeRemoteDataSource.getMemberList(loungeId)
            .onEach{
                loungeLocalDataSource.putMemberList(it, loungeId)
            }
            .launchIn(CoroutineScope(currentCoroutineContext()))
    }

    private suspend fun syncChatList(loungeId: String){
        loungeRemoteDataSource.getChatList(loungeId)
            .map { it.map(LoungeChatDataMapper::mapToRight) }
            .onEach{
                loungeLocalDataSource.putChatList(it.map(LoungeChatDataMapper::mapToLeft), loungeId)
            }
            .launchIn(CoroutineScope(currentCoroutineContext()))
    }
}