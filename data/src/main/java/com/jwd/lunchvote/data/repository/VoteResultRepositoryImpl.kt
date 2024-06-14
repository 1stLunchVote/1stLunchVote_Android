package com.jwd.lunchvote.data.repository

import com.jwd.lunchvote.data.mapper.asData
import com.jwd.lunchvote.data.mapper.asDomain
import com.jwd.lunchvote.data.source.remote.VoteResultDataSource
import com.jwd.lunchvote.domain.entity.FirstVoteResult
import com.jwd.lunchvote.domain.entity.SecondVoteResult
import com.jwd.lunchvote.domain.repository.VoteResultRepository
import javax.inject.Inject

class VoteResultRepositoryImpl @Inject constructor(
  private val voteResultDataSource: VoteResultDataSource
): VoteResultRepository {

  override suspend fun saveFirstVoteResult(firstVoteResult: FirstVoteResult) {
    voteResultDataSource.saveFirstVoteResult(firstVoteResult.asData())
  }

  override suspend fun getFirstVoteResultByLoungeId(loungeId: String): FirstVoteResult =
    voteResultDataSource.getFirstVoteResultByLoungeId(loungeId).asDomain()

  override suspend fun saveSecondVoteResult(secondVoteResult: SecondVoteResult) {
    voteResultDataSource.saveSecondVoteResult(secondVoteResult.asData())
  }

  override suspend fun getSecondVoteResultByLoungeId(loungeId: String): SecondVoteResult =
    voteResultDataSource.getSecondVoteResultByLoungeId(loungeId).asDomain()

  override suspend fun getAllVoteResults(): List<SecondVoteResult> =
    voteResultDataSource.getAllVoteResults().map { it.asDomain() }
}