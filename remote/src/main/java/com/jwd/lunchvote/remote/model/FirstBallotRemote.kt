package com.jwd.lunchvote.remote.model

data class FirstBallotRemote(
  val likedFoodIds: List<String> = emptyList(),
  val dislikedFoodIds: List<String> = emptyList()
)
