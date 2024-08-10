package com.jwd.lunchvote.domain.entity

import com.jwd.lunchvote.domain.entity.Chat.SystemMessageType.CREATE
import com.jwd.lunchvote.domain.entity.Chat.SystemMessageType.EXILE
import com.jwd.lunchvote.domain.entity.Chat.SystemMessageType.EXIT
import com.jwd.lunchvote.domain.entity.Chat.SystemMessageType.JOIN
import com.jwd.lunchvote.domain.entity.Chat.SystemMessageType.SETTING_MAX_MEMBERS
import com.jwd.lunchvote.domain.entity.Chat.SystemMessageType.SETTING_MIN_DISLIKE_FOODS
import com.jwd.lunchvote.domain.entity.Chat.SystemMessageType.SETTING_MIN_LIKE_FOODS
import com.jwd.lunchvote.domain.entity.Chat.SystemMessageType.SETTING_SECOND_VOTE_CANDIDATES
import com.jwd.lunchvote.domain.entity.Chat.SystemMessageType.SETTING_TIME_LIMIT
import kr.co.inbody.config.error.ChatError
import java.time.Instant
import java.util.UUID

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

  enum class SystemMessageType {
    CREATE,
    JOIN,
    EXIT,
    EXILE,

    SETTING_TIME_LIMIT,
    SETTING_MAX_MEMBERS,
    SETTING_SECOND_VOTE_CANDIDATES,
    SETTING_MIN_LIKE_FOODS,
    SETTING_MIN_DISLIKE_FOODS
  }

  data class Builder(
    var loungeId: String? = null,
    var user: User? = null,
    var member: Member? = null,
    var type: SystemMessageType? = null,
    var arg: String? = null
  ) {
    fun loungeId(loungeId: String) = apply { this.loungeId = loungeId }
    fun user(user: User) = apply { this.user = user }
    fun member(member: Member) = apply { this.member = member }
    fun type(type: SystemMessageType) = apply { this.type = type }
    fun arg(arg: String) = apply { this.arg = arg }

    fun build(): Chat {
      val loungeId = requireNotNull(loungeId) { "투표방 정보가 없습니다." }
      val type = requireNotNull(type) { "정의되지 않은 메세지 타입입니다." }

      if (type in listOf(JOIN, EXIT, EXILE)) {
        require(user != null || member != null) { "초대, 퇴장 또는 추방된 사용자 정보가 없습니다." }
      }
      if (type in listOf(SETTING_TIME_LIMIT, SETTING_MAX_MEMBERS, SETTING_SECOND_VOTE_CANDIDATES, SETTING_MIN_LIKE_FOODS, SETTING_MIN_DISLIKE_FOODS)) {
        require(arg != null) { "변경된 투표 설정 값이 없습니다." }
      }

      return Chat(
        loungeId = loungeId,
        id = UUID.randomUUID().toString(),
        userId = user?.id ?: member?.userId ?: "",
        userName = user?.name ?: member?.userName ?: "",
        userProfile = user?.profileImage ?: member?.userProfile ?: "",
        message = when(type) {
          CREATE -> "투표 방이 생성되었습니다."
          JOIN -> "님이 입장하였습니다."
          EXIT -> "님이 퇴장하였습니다."
          EXILE -> "님이 추방되었습니다."
          SETTING_TIME_LIMIT -> "투표 시간 제한이 ${arg}(으)로 변경되었습니다."
          SETTING_MAX_MEMBERS -> "최대 인원이 ${arg}으로 변경되었습니다."
          SETTING_SECOND_VOTE_CANDIDATES -> "2차 투표 후보 수가 ${arg}로 변경되었습니다."
          SETTING_MIN_LIKE_FOODS -> "좋아하는 음식 최소 선택 수가 ${arg}로 변경되었습니다."
          SETTING_MIN_DISLIKE_FOODS -> "싫어하는 음식 최소 선택 수가 ${arg}로 변경되었습니다."
        },
        type = Type.SYSTEM,
        createdAt = Instant.now().epochSecond
      )
    }
  }

  companion object {
    /**
     * Chat 객체를 생성하는 빌더입니다.
     *
     * loungeId와 type은 필수로 설정해야 합니다.
     *
     * type이 JOIN, EXIT, EXILE일 경우 user 또는 member를 설정해야 합니다.
     *
     * 또한, type이 SETTING_TIME_LIMIT, SETTING_MAX_MEMBERS, SETTING_SECOND_VOTE_CANDIDATES, SETTING_MIN_LIKE_FOODS, SETTING_MIN_DISLIKE_FOODS일 경우 arg를 설정해야 합니다.
     *
     * example:
     * ```
     * val chat = Chat.builder()
     *  .loungeId(loungeId)
     *  .user(user)
     *  .type(JOIN)
     *  .build()
     *  ```
     */
    fun builder(): Builder = Builder()
  }
}
