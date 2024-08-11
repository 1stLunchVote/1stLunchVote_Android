package com.jwd.lunchvote.domain.entity

import com.jwd.lunchvote.domain.entity.Chat.Type.DEFAULT
import com.jwd.lunchvote.domain.entity.Chat.Type.SYSTEM
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

  /**
   * Chat 객체를 생성하는 빌더입니다.
   *
   * example:
   * ```
   * val chat = Chat.Builder(loungeId)
   *  .join(user.name)
   *  .build()
   *  ```
   *
   * @param loungeId 투표 방 ID
   */
  class Builder(private val loungeId: String) {

    private var type: Type = DEFAULT

    private var member: Member? = null
    private var message: String? = null

    private var name: String? = null
    private var messageType: String? = null

    private var timeLimit: Int? = NO_VALUE
    private var maxMembers: Int? = NO_VALUE
    private var secondVoteCandidates: Int? = NO_VALUE
    private var minLikeFoods: Int? = NO_VALUE
    private var minDislikeFoods: Int? = NO_VALUE

    fun member(member: Member) = apply {
      this.type = DEFAULT
      this.member = member
    }
    fun message(message: String) = apply {
      this.type = DEFAULT
      this.message = message
    }

    fun create() = apply {
      this.type = SYSTEM
      this.messageType = CREATE
    }

    fun join(name: String) = apply {
      this.type = SYSTEM
      this.name = name
      this.messageType = JOIN
    }
    fun exit(name: String) = apply {
      this.type = SYSTEM
      this.name = name
      this.messageType = EXIT
    }
    fun exile(name: String) = apply {
      this.type = SYSTEM
      this.name = name
      this.messageType = EXILE
    }

    fun setTimeLimit(timeLimit: Int?) = apply {
      this.type = SYSTEM
      this.messageType = SETTING_TIME_LIMIT
      this.timeLimit = timeLimit
    }
    fun setMaxMembers(maxMembers: Int) = apply {
      this.type = SYSTEM
      this.messageType = SETTING_MAX_MEMBERS
      this.maxMembers = maxMembers
    }
    fun setSecondVoteCandidates(secondVoteCandidates: Int) = apply {
      this.type = SYSTEM
      this.messageType = SETTING_SECOND_VOTE_CANDIDATES
      this.secondVoteCandidates = secondVoteCandidates
    }
    fun setMinLikeFoods(minLikeFoods: Int?) = apply {
      this.type = SYSTEM
      this.messageType = SETTING_MIN_LIKE_FOODS
      this.minLikeFoods = minLikeFoods
    }
    fun setMinDislikeFoods(minDislikeFoods: Int?) = apply {
      this.type = SYSTEM
      this.messageType = SETTING_MIN_DISLIKE_FOODS
      this.minDislikeFoods = minDislikeFoods
    }

    fun build(): Chat =
      Chat(
        loungeId = loungeId,
        id = UUID.randomUUID().toString(),
        userId = when (type) {
          DEFAULT -> requireNotNull(member) { "사용자 정보가 없습니다." }.userId
          SYSTEM -> ""
        },
        userName = when (type) {
          DEFAULT -> requireNotNull(member) { "사용자 정보가 없습니다." }.userName
          SYSTEM -> when (messageType) {
            JOIN -> requireNotNull(name) { "입장한 사용자 정보가 없습니다." }
            EXIT -> requireNotNull(name) { "퇴장한 사용자 정보가 없습니다." }
            EXILE -> requireNotNull(name) { "추방된 사용자 정보가 없습니다." }
            else -> ""
          }
        },
        userProfile = when (type) {
          DEFAULT -> requireNotNull(member) { "사용자 정보가 없습니다." }.userProfile
          SYSTEM -> ""
        },
        message = when (type) {
          DEFAULT -> requireNotNull(message) { "메시지가 없습니다." }
          SYSTEM -> when (messageType) {
            CREATE -> "투표 방이 생성되었습니다."
            JOIN -> "님이 입장하였습니다."
            EXIT -> "님이 퇴장하였습니다."
            EXILE -> "님이 추방되었습니다."
            SETTING_TIME_LIMIT -> "투표 시간 제한이 ${if (timeLimit != null) "${timeLimit}초" else "무제한으"}로 변경되었습니다."
            SETTING_MAX_MEMBERS -> "최대 인원이 ${maxMembers}명으로 변경되었습니다."
            SETTING_SECOND_VOTE_CANDIDATES -> "2차 투표 후보 수가 ${secondVoteCandidates}개로 변경되었습니다."
            SETTING_MIN_LIKE_FOODS -> if (minLikeFoods != null) "좋아하는 음식 최소 선택 수가 ${minLikeFoods}개로 변경되었습니다."
            else "좋아하는 음식 최소 선택 제한이 해제되었습니다."
            SETTING_MIN_DISLIKE_FOODS -> if (minDislikeFoods != null) "싫어하는 음식 최소 선택 수가 ${minDislikeFoods}개로 변경되었습니다."
            else "싫어하는 음식 최소 선택 제한이 해제되었습니다."
            else -> throw IllegalArgumentException("지원하지 않는 메세지 타입입니다.")
          }
        },
        type = type,
        createdAt = Instant.now().epochSecond
      )

    companion object {
      const val CREATE = "CREATE"
      const val JOIN = "JOIN"
      const val EXIT = "EXIT"
      const val EXILE = "EXILE"
      const val SETTING_TIME_LIMIT = "SETTING_TIME_LIMIT"
      const val SETTING_MAX_MEMBERS = "SETTING_MAX_MEMBERS"
      const val SETTING_SECOND_VOTE_CANDIDATES = "SETTING_SECOND_VOTE_CANDIDATES"
      const val SETTING_MIN_LIKE_FOODS = "SETTING_MIN_LIKE_FOODS"
      const val SETTING_MIN_DISLIKE_FOODS = "SETTING_MIN_DISLIKE_FOODS"

      const val NO_VALUE = -1
    }
  }
}
