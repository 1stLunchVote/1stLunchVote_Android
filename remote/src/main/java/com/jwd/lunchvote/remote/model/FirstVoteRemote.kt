package com.jwd.lunchvote.remote.model

data class FirstVoteRemote(
  val likedFoodIds: List<String> = emptyList(),
  val dislikedFoodIds: List<String> = emptyList()
)
