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
import kotlinx.coroutines.delay
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
    CREATE, JOIN, EXIT, EXILE,

    SETTING_TIME_LIMIT, SETTING_MAX_MEMBERS, SETTING_SECOND_VOTE_CANDIDATES, SETTING_MIN_LIKE_FOODS, SETTING_MIN_DISLIKE_FOODS
  }

  class Builder(private val loungeId: String) {

    private var type: SystemMessageType? = null

    private var user: User? = null
    private var member: Member? = null

    private var timeLimit: Int? = NO_VALUE
    private var maxMembers: Int? = NO_VALUE
    private var secondVoteCandidates: Int? = NO_VALUE
    private var minLikeFoods: Int? = NO_VALUE
    private var minDislikeFoods: Int? = NO_VALUE

    fun type(type: SystemMessageType) = apply { this.type = type }

    fun user(user: User) = apply { this.user = user }
    fun member(member: Member) = apply { this.member = member }

    fun timeLimit(timeLimit: Int?) = apply { this.timeLimit = timeLimit }
    fun maxMembers(maxMembers: Int?) = apply { this.maxMembers = maxMembers }
    fun secondVoteCandidates(secondVoteCandidates: Int?) = apply { this.secondVoteCandidates = secondVoteCandidates }
    fun minLikeFoods(minLikeFoods: Int?) = apply { this.minLikeFoods = minLikeFoods }
    fun minDislikeFoods(minDislikeFoods: Int?) = apply { this.minDislikeFoods = minDislikeFoods }

    suspend fun build(): Chat {
      delay(100)

      val type = requireNotNull(type) { "정의되지 않은 메세지 타입입니다." }

      when (type) {
        CREATE -> Unit
        JOIN -> require(user != null || member != null) { "초대, 퇴장 또는 추방된 사용자 정보가 없습니다." }
        EXIT -> require(user != null || member != null) { "초대, 퇴장 또는 추방된 사용자 정보가 없습니다." }
        EXILE -> require(user != null || member != null) { "초대, 퇴장 또는 추방된 사용자 정보가 없습니다." }

        SETTING_TIME_LIMIT -> require(timeLimit != NO_VALUE) { "시간 제한이 설정되지 않았습니다." }
        SETTING_MAX_MEMBERS -> require(maxMembers != NO_VALUE && maxMembers != null) { "최대 인원이 설정되지 않았습니다." }
        SETTING_SECOND_VOTE_CANDIDATES -> require(secondVoteCandidates != NO_VALUE && secondVoteCandidates != null) { "2차 투표 후보 수가 설정되지 않았습니다." }
        SETTING_MIN_LIKE_FOODS -> require(minLikeFoods != NO_VALUE) { "좋아하는 음식 최소 선택 수가 설정되지 않았습니다." }
        SETTING_MIN_DISLIKE_FOODS -> require(minDislikeFoods != NO_VALUE) { "싫어하는 음식 최소 선택 수가 설정되지 않았습니다." }
      }

      return when (type) {
        CREATE -> Chat(
          loungeId = loungeId,
          id = UUID.randomUUID().toString(),
          userId = "",
          userName = "",
          userProfile = "",
          message = "투표 방이 생성되었습니다.",
          type = Type.SYSTEM,
          createdAt = Instant.now().epochSecond
        )
        JOIN -> Chat(
          loungeId = loungeId,
          id = UUID.randomUUID().toString(),
          userId = user?.id ?: member?.userId ?: "",
          userName = user?.name ?: member?.userName ?: "",
          userProfile = user?.profileImage ?: member?.userProfile ?: "",
          message = "님이 입장하였습니다.",
          type = Type.SYSTEM,
          createdAt = Instant.now().epochSecond
        )
        EXIT -> Chat(
          loungeId = loungeId,
          id = UUID.randomUUID().toString(),
          userId = user?.id ?: member?.userId ?: "",
          userName = user?.name ?: member?.userName ?: "",
          userProfile = user?.profileImage ?: member?.userProfile ?: "",
          message = "님이 퇴장하였습니다.",
          type = Type.SYSTEM,
          createdAt = Instant.now().epochSecond
        )
        EXILE -> Chat(
          loungeId = loungeId,
          id = UUID.randomUUID().toString(),
          userId = user?.id ?: member?.userId ?: "",
          userName = user?.name ?: member?.userName ?: "",
          userProfile = user?.profileImage ?: member?.userProfile ?: "",
          message = "님이 추방되었습니다.",
          type = Type.SYSTEM,
          createdAt = Instant.now().epochSecond
        )
        SETTING_TIME_LIMIT -> Chat(
          loungeId = loungeId,
          id = UUID.randomUUID().toString(),
          userId = "",
          userName = "",
          userProfile = "",
          message = "투표 시간 제한이 ${if (timeLimit != null) "${timeLimit}초" else "무제한으"}로 변경되었습니다.",
          type = Type.SYSTEM,
          createdAt = Instant.now().epochSecond
        )
        SETTING_MAX_MEMBERS -> Chat(
          loungeId = loungeId,
          id = UUID.randomUUID().toString(),
          userId = "",
          userName = "",
          userProfile = "",
          message = "최대 인원이 ${maxMembers}명으로 변경되었습니다.",
          type = Type.SYSTEM,
          createdAt = Instant.now().epochSecond
        )
        SETTING_SECOND_VOTE_CANDIDATES -> Chat(
          loungeId = loungeId,
          id = UUID.randomUUID().toString(),
          userId = "",
          userName = "",
          userProfile = "",
          message = "2차 투표 후보 수가 ${secondVoteCandidates}개로 변경되었습니다.",
          type = Type.SYSTEM,
          createdAt = Instant.now().epochSecond
        )
        SETTING_MIN_LIKE_FOODS -> Chat(
          loungeId = loungeId,
          id = UUID.randomUUID().toString(),
          userId = "",
          userName = "",
          userProfile = "",
          message = if (minLikeFoods != null) "좋아하는 음식 최소 선택 수가 ${minLikeFoods}개로 변경되었습니다."
                    else "좋아하는 음식 최소 선택 제한이 해제되었습니다.",
          type = Type.SYSTEM,
          createdAt = Instant.now().epochSecond
        )
        SETTING_MIN_DISLIKE_FOODS -> Chat(
          loungeId = loungeId,
          id = UUID.randomUUID().toString(),
          userId = "",
          userName = "",
          userProfile = "",
          message = if (minDislikeFoods != null) "싫어하는 음식 최소 선택 수가 ${minDislikeFoods}개로 변경되었습니다."
                    else "싫어하는 음식 최소 선택 제한이 해제되었습니다.",
          type = Type.SYSTEM,
          createdAt = Instant.now().epochSecond
        )
      }
    }

    companion object {
      const val NO_VALUE = -1
    }
  }

  companion object {
    /**
     * Chat 객체를 생성하는 빌더입니다.
     *
     * loungeId와 type은 필수로 설정해야 합니다.
     *
     * 유저가 입장, 퇴장, 추방된 경우 user 또는 member를 설정해야 합니다.
     *
     * 또한, 투표 설정을 변경한 경우 설정 값을 설정해야 합니다.
     *
     * example:
     * ```
     * val chat = Chat.builder(loungeId)
     *  .type(JOIN)
     *  .user(user)
     *  .build()
     *  ```
     */
    fun builder(loungeId: String): Builder = Builder(loungeId)
  }
}
