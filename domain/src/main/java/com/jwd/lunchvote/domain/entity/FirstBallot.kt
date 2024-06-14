package com.jwd.lunchvote.domain.entity

data class FirstBallot(
  val loungeId: String,
  val userId: String,
  val likedFoodIds: List<String>,
  val dislikedFoodIds: List<String>
)
