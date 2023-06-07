package com.jwd.lunchvote.data.repository

import com.jwd.lunchvote.data.room.entity.ChatEntity
import com.jwd.lunchvote.data.room.entity.MemberEntity
import com.jwd.lunchvote.data.source.local.lounge.LoungeLocalDataSource
import com.jwd.lunchvote.data.source.remote.lounge.LoungeRemoteDataSource
import com.jwd.lunchvote.domain.entity.LoungeChat
import com.jwd.lunchvote.domain.entity.Member
import com.jwd.lunchvote.domain.repository.LoungeRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

class LoungeRepositoryImpl @Inject constructor(
    private val loungeLocalDataSource: LoungeLocalDataSource,
    private val loungeRemoteDataSource: LoungeRemoteDataSource
): LoungeRepository {
    override fun createLounge(): Flow<String> {
        return loungeRemoteDataSource.createLounge().map {
            it ?: throw Exception("Failed to create lounge")
        }
    }

    override fun joinLounge(loungeId: String): Flow<Unit> {
        return loungeRemoteDataSource.joinLounge(loungeId)
    }

    override fun getMemberList(loungeId: String): Flow<List<Member>> {
        return loungeLocalDataSource.getMemberList(loungeId)
            .map { it.map(MemberEntity::toDomain) }
            .onStart { syncMemberList(loungeId) }
    }

    override fun getChatList(loungeId: String): Flow<List<LoungeChat>> {
        return loungeLocalDataSource.getChatList(loungeId)
            .map { it.map(ChatEntity::toDomain) }
            .onStart { syncChatList(loungeId) }
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