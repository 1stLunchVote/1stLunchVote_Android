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

  class Builder(private val loungeId: String) {

    private var user: User? = null
    private var type: Type = Type.DEFAULT

    fun user(user: User) = apply { this.user = user }
    fun owner() = apply { this.type = Type.OWNER }

    fun build(): Member {
      val user = requireNotNull(user) { "사용자 정보가 없습니다." }

      return Member(
        loungeId = loungeId,
        userId = user.id,
        userName = user.name,
        userProfile = user.profileImage,
        type = type,
        status = Status.STANDBY,
        createdAt = System.currentTimeMillis(),
        deletedAt = null
      )
    }
  }

  companion object {
    /**
     * Member 객체를 생성하는 빌더입니다.
     *
     * user는 필수로 설정해야 합니다.
     *
     * 방장의 경우, owner()를 호출하여 설정할 수 있습니다.
     *
     * example.
     * ```
     * val member = Member.builder(loungeId)
     *  .user(user)
     *  .owner()
     *  .build()
     *  ```
     *
     *  @param loungeId 투표 방 ID
     */
    fun builder(loungeId: String) = Builder(loungeId)
  }
}