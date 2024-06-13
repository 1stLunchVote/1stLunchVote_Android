package com.jwd.lunchvote.data.source.remote

import com.jwd.lunchvote.data.model.SecondVoteData

interface SecondVoteDataSource {

  suspend fun createSecondVote(loungeId: String, foodIds: List<String>)
  suspend fun getElectedFoodIdsByLoungeId(loungeId: String): List<String>
  suspend fun submitVote(loungeId: String, userId: String, foodId: String)
  suspend fun getSecondVoteResult(loungeId: String): SecondVoteData
}