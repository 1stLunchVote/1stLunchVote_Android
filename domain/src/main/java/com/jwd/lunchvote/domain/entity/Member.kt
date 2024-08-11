package com.jwd.lunchvote.domain.entity

import com.jwd.lunchvote.domain.entity.Member.Status.STANDBY
import com.jwd.lunchvote.domain.entity.Member.Type.DEFAULT
import com.jwd.lunchvote.domain.entity.Member.Type.OWNER

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

  /**
   * Member 객체를 생성하는 빌더입니다.
   *
   * 방장의 경우, owner()를 호출하여 설정할 수 있습니다.
   *
   * example:
   * ```
   * val member = Member.Builder(loungeId)
   *  .user(user)
   *  .owner()
   *  .build()
   *  ```
   *
   *  @param loungeId 투표 방 ID
   *  @param user 유저 객체
   */
  class Builder(
    private val loungeId: String,
    private val user: User
  ) {

    private var type: Type = DEFAULT

    fun owner() = apply { this.type = OWNER }

    fun build(): Member =
      Member(
        loungeId = loungeId,
        userId = user.id,
        userName = user.name,
        userProfile = user.profileImage,
        type = type,
        status = STANDBY,
        createdAt = System.currentTimeMillis(),
        deletedAt = null
      )
  }
}