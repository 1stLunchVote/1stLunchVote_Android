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

  companion object {
    const val CREATE_SYSTEM_MESSAGE = "투표 방이 생성되었습니다."
    const val JOIN_SYSTEM_MESSAGE = "님이 입장하였습니다."
    const val EXIT_SYSTEM_MESSAGE = "님이 퇴장하였습니다."
    const val EXILE_SYSTEM_MESSAGE = "님이 추방되었습니다."
  }
}
