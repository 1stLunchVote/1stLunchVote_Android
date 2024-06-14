package com.jwd.lunchvote.domain.entity

data class VoteResult(
  val loungeId: String,
  val foodId: String,
  val voteRatio: Float
)
