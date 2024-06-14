package com.jwd.lunchvote.domain.repository

import com.jwd.lunchvote.domain.entity.VoteResult

interface VoteResultRepository {

  suspend fun saveVoteResult(voteResult: VoteResult)
  suspend fun getVoteResultByLoungeId(loungeId: String): VoteResult
  suspend fun getAllVoteResults(): List<VoteResult>
}