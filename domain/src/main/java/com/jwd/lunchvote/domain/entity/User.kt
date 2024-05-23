package com.jwd.lunchvote.domain.entity

data class User(
  val id: String,
  val email: String,
  val name: String,
  val profileImage: String,
  val createdAt: Long,
  val deletedAt: Long?
)
