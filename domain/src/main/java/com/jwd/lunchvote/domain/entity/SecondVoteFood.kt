package com.jwd.lunchvote.domain.entity

data class SecondVoteFood(
  val foodId: String,
  val userIds: List<String>
)
