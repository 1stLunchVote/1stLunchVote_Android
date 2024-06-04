package com.jwd.lunchvote.domain.entity

data class FirstVoteResult(
  val loungeId: String,
  val likedFoodIds: List<String>,
  val dislikedFoodIds: List<String>
)
