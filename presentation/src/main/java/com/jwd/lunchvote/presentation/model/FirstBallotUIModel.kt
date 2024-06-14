package com.jwd.lunchvote.presentation.model

data class FirstBallotUIModel(
  val loungeId: String = "",
  val userId: String = "",
  val likedFoodIds: List<String> = emptyList(),
  val dislikedFoodIds: List<String> = emptyList()
)
