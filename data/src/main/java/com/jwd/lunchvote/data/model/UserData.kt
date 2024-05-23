package com.jwd.lunchvote.data.model

data class UserData(
  val id: String,
  val email: String,
  val name: String,
  val profileImage: String,
  val createdAt: Long,
  val deletedAt: Long?
)
