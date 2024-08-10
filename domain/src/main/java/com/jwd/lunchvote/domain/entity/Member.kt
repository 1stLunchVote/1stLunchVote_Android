package com.jwd.lunchvote.domain.entity

data class Member(
  val loungeId: String,
  val userId: String,
  val userName: String,
  val userProfile: String,
  val type: Type,
  val status: Status,
  val createdAt: Long,
  val deletedAt: Long?
) {

  enum class Type {
    DEFAULT, OWNER, READY, LEAVED, EXILED
  }

  enum class Status {
    STANDBY, VOTING, VOTED
  }
}