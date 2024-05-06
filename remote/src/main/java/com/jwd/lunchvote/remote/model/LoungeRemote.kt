package com.jwd.lunchvote.remote.model

data class LoungeRemote(
  val status: String = "",
  val members: List<MemberRemote> = emptyList()
)
