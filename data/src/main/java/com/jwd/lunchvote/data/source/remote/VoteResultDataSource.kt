package com.jwd.lunchvote.data.source.remote

import com.jwd.lunchvote.data.model.VoteResultData

interface VoteResultDataSource {

  suspend fun saveVoteResult(voteResult: VoteResultData)
  suspend fun getVoteResultByLoungeId(loungeId: String): VoteResultData
  suspend fun getAllVoteResults(): List<VoteResultData>
}