package com.jwd.lunchvote.data.model

data class ChatData(
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