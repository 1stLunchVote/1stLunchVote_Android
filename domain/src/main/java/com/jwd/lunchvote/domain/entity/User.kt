package com.jwd.lunchvote.domain.entity

data class User(
  val id: String,
  val email: String,
  val name: String,
  val profileImageUrl: String,
  val createdAt: String
)
