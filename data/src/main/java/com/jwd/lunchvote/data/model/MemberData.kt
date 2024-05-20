package com.jwd.lunchvote.data.model

data class MemberData(
  val loungeId: String,
  val userId: String,
  val userName: String,
  val userProfile: String,
  val type: Type,
  val createdAt: Long,
  val deletedAt: Long?
) {

  enum class Type {
    DEFAULT, OWNER, READY, EXILED
  }
}