package com.jwd.lunchvote.domain.repository

import com.jwd.lunchvote.domain.entity.SecondVote

interface SecondVoteRepository {

  suspend fun createSecondVote(loungeId: String, foodIds: List<String>)
  suspend fun getElectedFoodIdsByLoungeId(loungeId: String): List<String>
  suspend fun submitVote(loungeId: String, userId: String, foodId: String)
  suspend fun getSecondVoteResult(loungeId: String): SecondVote
}