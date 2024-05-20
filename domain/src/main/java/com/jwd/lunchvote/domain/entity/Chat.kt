package com.jwd.lunchvote.domain.entity

data class Chat(
  val loungeId: String,
  val id: String,
  val userId: String,
  val userName: String,
  val userProfile: String,
  val message: String,
  val type: Type,
  val createdAt: Long
) {

  enum class Type {
    DEFAULT, SYSTEM
  }
}
