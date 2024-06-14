package com.jwd.lunchvote.data.source.remote

import com.jwd.lunchvote.data.model.FirstBallotData
import com.jwd.lunchvote.data.model.SecondBallotData

interface BallotDataSource {

  suspend fun submitFirstBallot(firstBallot: FirstBallotData)
  suspend fun getAllFirstBallotByLoungeId(loungeId: String): List<FirstBallotData>
  suspend fun submitSecondBallot(secondBallot: SecondBallotData)
  suspend fun getAllSecondBallotByLoungeId(loungeId: String): List<SecondBallotData>
}
