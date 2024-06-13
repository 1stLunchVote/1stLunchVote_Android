package com.jwd.lunchvote.domain.entity

data class SecondVote(
  val loungeId: String,
  val foods: List<SecondVoteFood>
)
