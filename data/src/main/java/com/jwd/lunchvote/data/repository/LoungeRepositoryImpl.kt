package com.jwd.lunchvote.data.repository

import com.jwd.lunchvote.data.room.entity.ChatEntity
import com.jwd.lunchvote.data.room.entity.MemberEntity
import com.jwd.lunchvote.data.source.local.lounge.LoungeLocalDataSource
import com.jwd.lunchvote.data.source.remote.lounge.LoungeRemoteDataSource
import com.jwd.lunchvote.data.worker.SendWorkerManager
import com.jwd.lunchvote.domain.entity.LoungeChat
import com.jwd.lunchvote.domain.entity.Member
import com.jwd.lunchvote.domain.repository.LoungeRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flowOf
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
    @OptIn(FlowPreview::class)
    override fun createLounge(): Flow<String> {
        return loungeRemoteDataSource.createLounge().map {
            it ?: throw Exception("Failed to create lounge")
        }.flatMapConcat {id ->
            loungeRemoteDataSource.sendChat(id, null, 1).map { id }
        }
    }

    @OptIn(FlowPreview::class)
    override fun joinLounge(loungeId: String): Flow<Unit> {
        return loungeRemoteDataSource.joinLounge(loungeId).flatMapConcat {
            if (it != null) {
                loungeRemoteDataSource.sendChat(loungeId, null, 2)
            } else {
                flowOf(Unit)
            }
        }
    }

    override fun getMemberList(loungeId: String): Flow<List<Member>> {
        return loungeLocalDataSource.getMemberList(loungeId)
            .filter { it.isNotEmpty() }
            .map { it.map(MemberEntity::toDomain) }
            .onStart { syncMemberList(loungeId) }
    }

    override fun getChatList(loungeId: String): Flow<List<LoungeChat>> {
        Timber.e(LocalDateTime.now().toString())
        return loungeLocalDataSource.getChatList(loungeId)
            .filter {
                it.isNotEmpty()
            }
            .map { it.map(ChatEntity::toDomain) }
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