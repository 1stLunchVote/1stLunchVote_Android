package com.jwd.lunchvote.domain.repository

import com.jwd.lunchvote.domain.entity.FirstVote

interface FirstVoteRepository {

  suspend fun submitVote(loungeId: String, userId: String, likedFoodIds: List<String>, dislikedFoodIds: List<String>)
  suspend fun getAllFirstVotes(loungeId: String): List<FirstVote>
}