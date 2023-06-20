package com.jwd.lunchvote.data.repository

import com.jwd.lunchvote.data.source.local.LoungeLocalDataSource
import com.jwd.lunchvote.data.source.remote.LoungeRemoteDataSource
import com.jwd.lunchvote.data.worker.SendWorkerManager
import com.jwd.lunchvote.domain.entity.LoungeChat
import com.jwd.lunchvote.domain.entity.Member
import com.jwd.lunchvote.domain.entity.MemberStatus
import com.jwd.lunchvote.domain.repository.LoungeRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import timber.log.Timber
import java.time.LocalDateTime
import javax.inject.Inject

class LoungeRepositoryImpl @Inject constructor(
    private val sendWorkerManager: SendWorkerManager,
    private val loungeLocalDataSource: LoungeLocalDataSource,
    private val loungeRemoteDataSource: LoungeRemoteDataSource
): LoungeRepository {
    override fun checkLoungeExist(loungeId: String): Flow<Boolean> {
        return loungeRemoteDataSource.checkLoungeExist(loungeId)
    }

    @OptIn(FlowPreview::class)
    override fun createLounge(): Flow<String> {
        return loungeRemoteDataSource.createLounge().map {
            it ?: throw Exception("Failed to create lounge")
        }.flatMapMerge {id ->
            loungeRemoteDataSource.sendChat(id, null, 1).map { id }
        }
    }

    @OptIn(FlowPreview::class)
    override fun joinLounge(loungeId: String): Flow<Unit> {
        return loungeRemoteDataSource.joinLounge(loungeId).flatMapMerge {
            loungeRemoteDataSource.sendChat(loungeId, null, 2)
        }
    }

    override fun getMemberList(loungeId: String): Flow<List<Member>> {
        return loungeLocalDataSource.getMemberList(loungeId)
            .filter { it.isNotEmpty() }
            .onStart { syncMemberList(loungeId) }
    }

    override fun getChatList(loungeId: String): Flow<List<LoungeChat>> {
        Timber.e(LocalDateTime.now().toString())
        return loungeLocalDataSource.getChatList(loungeId)
            .filter {
                it.isNotEmpty()
            }
            .onStart { syncChatList(loungeId) }
    }

    override fun sendChat(loungeId: String, content: String): Flow<Unit> {
        sendWorkerManager.startSendWork(loungeId, content)
        return loungeLocalDataSource.insertChat(loungeId, content, 0)
    }

    @OptIn(FlowPreview::class)
    override fun updateReady(uid: String, loungeId: String): Flow<Unit> {
        return loungeLocalDataSource.updateMemberReady(uid, loungeId)
            .flatMapMerge {
                loungeRemoteDataSource.updateReady(uid, loungeId)
            }
    }

    @OptIn(FlowPreview::class)
    override fun exitLounge(uid: String, loungeId: String): Flow<Unit> {
        return loungeRemoteDataSource.exitLounge(uid, loungeId)
            .flatMapMerge {
                loungeRemoteDataSource.sendChat(loungeId, null, 3)
            }
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
            .onEach{
                loungeLocalDataSource.putChatList(it, loungeId)
            }
            .launchIn(CoroutineScope(currentCoroutineContext()))
    }
}