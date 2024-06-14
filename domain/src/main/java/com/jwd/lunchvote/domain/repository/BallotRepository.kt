package com.jwd.lunchvote.domain.repository

import com.jwd.lunchvote.domain.entity.FirstBallot
import com.jwd.lunchvote.domain.entity.SecondBallot

interface BallotRepository {

  suspend fun submitFirstBallot(firstBallot: FirstBallot)
  suspend fun getAllFirstBallotByLoungeId(loungeId: String): List<FirstBallot>
  suspend fun submitSecondBallot(secondBallot: SecondBallot)
  suspend fun getAllSecondBallotByLoungeId(loungeId: String): List<SecondBallot>
}