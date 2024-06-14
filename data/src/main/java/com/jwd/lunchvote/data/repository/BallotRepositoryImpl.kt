package com.jwd.lunchvote.data.repository

import com.jwd.lunchvote.data.mapper.asData
import com.jwd.lunchvote.data.mapper.asDomain
import com.jwd.lunchvote.data.source.remote.BallotDataSource
import com.jwd.lunchvote.domain.entity.FirstBallot
import com.jwd.lunchvote.domain.entity.SecondBallot
import com.jwd.lunchvote.domain.repository.BallotRepository
import javax.inject.Inject

class BallotRepositoryImpl @Inject constructor(
  private val ballotDataSource: BallotDataSource
) : BallotRepository {

  override suspend fun submitFirstBallot(firstBallot: FirstBallot) {
    ballotDataSource.submitFirstBallot(firstBallot.asData())
  }

  override suspend fun getAllFirstBallotByLoungeId(loungeId: String): List<FirstBallot> =
    ballotDataSource.getAllFirstBallotByLoungeId(loungeId).map { it.asDomain() }

  override suspend fun submitSecondBallot(secondBallot: SecondBallot) {
    ballotDataSource.submitSecondBallot(secondBallot.asData())
  }

  override suspend fun getAllSecondBallotByLoungeId(loungeId: String): List<SecondBallot> =
    ballotDataSource.getAllSecondBallotByLoungeId(loungeId).map { it.asDomain() }
}