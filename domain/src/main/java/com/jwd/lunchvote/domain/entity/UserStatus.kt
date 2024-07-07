package com.jwd.lunchvote.domain.entity

data class UserStatus(
  val userId: String,
  val lastOnline: Long?,
  val loungeId: String?
)
