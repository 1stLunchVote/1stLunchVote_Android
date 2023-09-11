package com.jwd.lunchvote.data.repository

import com.jwd.lunchvote.data.mapper.LoungeChatDataMapper
import com.jwd.lunchvote.data.mapper.MemberDataMapper
import com.jwd.lunchvote.data.mapper.type.MemberStatusDataTypeMapper
import com.jwd.lunchvote.data.model.type.MessageDataType
import com.jwd.lunchvote.data.source.local.LoungeLocalDataSource
import com.jwd.lunchvote.data.source.remote.LoungeRemoteDataSource
import com.jwd.lunchvote.data.worker.SendWorkerManager
import com.jwd.lunchvote.domain.entity.LoungeChat
import com.jwd.lunchvote.domain.entity.Member
import com.jwd.lunchvote.domain.entity.type.MemberStatusType
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
    private val local: LoungeLocalDataSource,
    private val remote: LoungeRemoteDataSource
): LoungeRepository {
    override suspend fun checkLoungeExist(loungeId: String): Boolean {
        return remote.checkLoungeExist(loungeId)
    }

    override suspend fun createLounge(): String {
        val id = UUID.randomUUID().toString()
        val loungeId = remote.createLounge()
        remote.sendChat(id, loungeId, null, MessageDataType.CREATE)
        local.updateCurrentLounge(loungeId)
        return loungeId
    }

    override suspend fun joinLounge(loungeId: String) {
        val id = UUID.randomUUID().toString()
        remote.joinLounge(loungeId)
        remote.sendChat(id, loungeId, null, MessageDataType.JOIN)
        local.updateCurrentLounge(loungeId)
    }

    override fun getMemberList(loungeId: String): Flow<List<Member>> {
        return local.getMemberList(loungeId)
            .filter { it.isNotEmpty() }
            .map { it.map(MemberDataMapper::mapToRight) }
            .onStart { syncMemberList(loungeId) }
    }

    override fun getChatList(loungeId: String): Flow<List<LoungeChat>> {
        return local.getChatList(loungeId)
            .map{it.map(LoungeChatDataMapper::mapToRight)}
            .filter { it.isNotEmpty() }
            .onStart { syncChatList(loungeId) }
    }

    // 일반 채팅 메시지 보내는 경우
    override suspend fun sendChat(loungeId: String, content: String) {
        val id = UUID.randomUUID().toString()
        sendWorkerManager.startSendWork(id, loungeId, content)
        return local.insertChat(id, loungeId, content, MessageDataType.NORMAL)
    }

    override suspend fun updateReady(uid: String, loungeId: String) {
        local.updateMemberReady(uid, loungeId)
        remote.updateReady(uid, loungeId)
    }

    override suspend fun exitLounge(uid: String, loungeId: String) {
        val id = UUID.randomUUID().toString()
        remote.sendChat(id, loungeId, null, MessageDataType.EXIT)
        remote.exitLounge(uid, loungeId)
        local.deleteAllChat(loungeId)
        local.deleteCurrentLounge()
    }

    override fun getMemberStatus(uid: String, loungeId: String): Flow<MemberStatusType>{
        return remote.getMemberList(loungeId)
            .map {
                if (it.isEmpty()){
                    MemberStatusType.EXITED
                }
                else it.find { member -> member.id == uid }?.status?.let(MemberStatusDataTypeMapper::mapToRight)
                    ?: MemberStatusType.EXITED
            }
    }

    override fun getCurrentLounge(): Flow<String?> {
        return local.getCurrentLounge()
    }


    private suspend fun syncMemberList(loungeId: String){
        remote.getMemberList(loungeId)
            .onEach{
                local.putMemberList(it, loungeId)
            }
            .launchIn(CoroutineScope(currentCoroutineContext()))
    }

    private suspend fun syncChatList(loungeId: String){
        remote.getChatList(loungeId)
            .map { it.map(LoungeChatDataMapper::mapToRight) }
            .onEach{
                local.putChatList(it.map(LoungeChatDataMapper::mapToLeft), loungeId)
            }
            .launchIn(CoroutineScope(currentCoroutineContext()))
    }
}