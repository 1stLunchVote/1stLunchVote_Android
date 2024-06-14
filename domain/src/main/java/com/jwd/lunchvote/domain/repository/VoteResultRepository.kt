package com.jwd.lunchvote.domain.repository

import com.jwd.lunchvote.domain.entity.FirstVoteResult
import com.jwd.lunchvote.domain.entity.SecondVoteResult

interface VoteResultRepository {

  suspend fun saveFirstVoteResult(firstVoteResult: FirstVoteResult)
  suspend fun getFirstVoteResultByLoungeId(loungeId: String): FirstVoteResult
  suspend fun saveSecondVoteResult(secondVoteResult: SecondVoteResult)
  suspend fun getSecondVoteResultByLoungeId(loungeId: String): SecondVoteResult
  suspend fun getAllVoteResults(): List<SecondVoteResult>
}