package com.jwd.lunchvote.data.model

data class FirstBallotData(
  val loungeId: String,
  val userId: String,
  val likedFoodIds: List<String>,
  val dislikedFoodIds: List<String>
)
