package com.jwd.lunchvote.data.model

data class FriendData(
  val id: String,
  val userId: String,
  val friendId: String,
  val createdAt: Long,
  val deletedAt: Long?
)