package com.jwd.lunchvote.data.model

data class UserStatusData(
  val userId: String,
  val lastOnline: Long?,
  val loungeId: String?
)
