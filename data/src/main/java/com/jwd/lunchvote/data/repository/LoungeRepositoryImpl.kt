package com.jwd.lunchvote.data.repository

import com.jwd.lunchvote.data.source.remote.lounge.LoungeRemoteDataSource
import com.jwd.lunchvote.domain.entity.Member
import com.jwd.lunchvote.domain.repository.LoungeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class LoungeRepositoryImpl @Inject constructor(
    private val loungeRemoteDataSource: LoungeRemoteDataSource
): LoungeRepository {
    override fun createLounge(): Flow<String> {
        return loungeRemoteDataSource.createLounge().map {
            it ?: throw Exception("Failed to create lounge")
        }
    }

    override fun getMemberList(loungeId: String): Flow<List<Member>> {
        return loungeRemoteDataSource.getMemberList(loungeId)
    }
}