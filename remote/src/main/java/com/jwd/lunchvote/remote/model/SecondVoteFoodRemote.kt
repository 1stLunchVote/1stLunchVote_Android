package com.jwd.lunchvote.remote.model

data class SecondVoteFoodRemote(
  val foodId: String = "",
  val userIds: List<String> = emptyList()
)
