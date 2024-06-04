package com.jwd.lunchvote.presentation.model

data class FirstVoteUIModel(
  val loungeId: String = "",
  val userId: String = "",
  val likedFoodIds: List<String> = emptyList(),
  val dislikedFoodIds: List<String> = emptyList()
)
