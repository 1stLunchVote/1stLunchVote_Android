package com.jwd.lunchvote.domain.entity

data class FirstVote(
  val loungeId: String,
  val userId: String,
  val likedFoodIds: List<String>,
  val dislikedFoodIds: List<String>
)
