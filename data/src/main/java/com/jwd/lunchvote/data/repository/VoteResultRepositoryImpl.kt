package com.jwd.lunchvote.data.repository

import com.jwd.lunchvote.data.mapper.asData
import com.jwd.lunchvote.data.mapper.asDomain
import com.jwd.lunchvote.data.source.remote.VoteResultDataSource
import com.jwd.lunchvote.domain.entity.VoteResult
import com.jwd.lunchvote.domain.repository.VoteResultRepository
import javax.inject.Inject

class VoteResultRepositoryImpl @Inject constructor(
  private val voteResultDataSource: VoteResultDataSource
): VoteResultRepository {

  override suspend fun saveVoteResult(voteResult: VoteResult) {
    voteResultDataSource.saveVoteResult(voteResult.asData())
  }

  override suspend fun getVoteResultByLoungeId(loungeId: String): VoteResult =
    voteResultDataSource.getVoteResultByLoungeId(loungeId).asDomain()

  override suspend fun getAllVoteResults(): List<VoteResult> =
    voteResultDataSource.getAllVoteResults().map { it.asDomain() }
}