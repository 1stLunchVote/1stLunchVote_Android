package com.jwd.lunchvote.data.source.remote

import com.jwd.lunchvote.data.model.FirstVoteData

interface FirstVoteDataSource {

  suspend fun submitVote(loungeId: String, userId: String, likedFoodIds: List<String>, dislikedFoodIds: List<String>)
  suspend fun getAllFirstVotes(loungeId: String): List<FirstVoteData>
}