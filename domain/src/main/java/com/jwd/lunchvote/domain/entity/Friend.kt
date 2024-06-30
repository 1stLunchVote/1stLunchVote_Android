package com.jwd.lunchvote.domain.entity

data class Friend(
  val id: String,
  val userId: String,
  val friendId: String,
  val createdAt: Long,
  val deletedAt: Long?
)