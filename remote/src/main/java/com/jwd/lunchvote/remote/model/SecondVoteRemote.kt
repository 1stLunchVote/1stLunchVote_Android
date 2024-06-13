package com.jwd.lunchvote.remote.model

data class SecondVoteRemote(
  val loungeId: String = "",
  val foods: List<SecondVoteFoodRemote> = emptyList()
)
