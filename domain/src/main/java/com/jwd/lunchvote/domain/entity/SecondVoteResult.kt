package com.jwd.lunchvote.domain.entity

data class SecondVoteResult(
  val loungeId: String,
  val foodId: String,
  val voteRatio: Float
)
