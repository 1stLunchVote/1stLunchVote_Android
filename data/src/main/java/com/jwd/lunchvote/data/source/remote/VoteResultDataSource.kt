package com.jwd.lunchvote.data.source.remote

import com.jwd.lunchvote.data.model.FirstVoteResultData
import com.jwd.lunchvote.data.model.SecondVoteResultData

interface VoteResultDataSource {

  suspend fun saveFirstVoteResult(firstVoteResult: FirstVoteResultData)
  suspend fun getFirstVoteResultByLoungeId(loungeId: String): FirstVoteResultData
  suspend fun saveSecondVoteResult(secondVoteResult: SecondVoteResultData)
  suspend fun getSecondVoteResultByLoungeId(loungeId: String): SecondVoteResultData
  suspend fun getAllVoteResults(): List<SecondVoteResultData>
}