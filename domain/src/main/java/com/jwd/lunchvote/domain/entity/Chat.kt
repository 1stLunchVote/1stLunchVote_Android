package com.jwd.lunchvote.domain.entity

import com.jwd.lunchvote.domain.entity.Chat.Type.SYSTEM
import kotlinx.coroutines.delay
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
   *  .join(user)
   *  .build()
   *  ```
   *
   * @param loungeId 투표 방 ID
   */
  class Builder(private val loungeId: String) {

    private var type: String = CREATE

    private var user: User? = null
    private var member: Member? = null

    private var timeLimit: Int? = NO_VALUE
    private var maxMembers: Int? = NO_VALUE
    private var secondVoteCandidates: Int? = NO_VALUE
    private var minLikeFoods: Int? = NO_VALUE
    private var minDislikeFoods: Int? = NO_VALUE

    fun create() = apply { this.type = CREATE }

    fun join(user: User) = apply {
      this.type = JOIN
      this.user = user
    }
    fun join(member: Member) = apply {
      this.type = JOIN
      this.member = member
    }
    fun exit(user: User) = apply {
      this.type = EXIT
      this.user = user
    }
    fun exit(member: Member) = apply {
      this.type = EXIT
      this.member = member
    }
    fun exile(user: User) = apply {
      this.type = EXILE
      this.user = user
    }
    fun exile(member: Member) = apply {
      this.type = EXILE
      this.member = member
    }

    fun setTimeLimit(timeLimit: Int?) = apply {
      this.type = SETTING_TIME_LIMIT
      this.timeLimit = timeLimit
    }
    fun setMaxMembers(maxMembers: Int?) = apply {
      this.type = SETTING_MAX_MEMBERS
      this.maxMembers = maxMembers
    }
    fun setSecondVoteCandidates(secondVoteCandidates: Int?) = apply {
      this.type = SETTING_SECOND_VOTE_CANDIDATES
      this.secondVoteCandidates = secondVoteCandidates
    }
    fun setMinLikeFoods(minLikeFoods: Int?) = apply {
      this.type = SETTING_MIN_LIKE_FOODS
      this.minLikeFoods = minLikeFoods
    }
    fun setMinDislikeFoods(minDislikeFoods: Int?) = apply {
      this.type = SETTING_MIN_DISLIKE_FOODS
      this.minDislikeFoods = minDislikeFoods
    }

    suspend fun build(): Chat {
      delay(100)

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
          type = SYSTEM,
          createdAt = Instant.now().epochSecond
        )
        JOIN -> Chat(
          loungeId = loungeId,
          id = UUID.randomUUID().toString(),
          userId = user?.id ?: member?.userId ?: "",
          userName = user?.name ?: member?.userName ?: "",
          userProfile = user?.profileImage ?: member?.userProfile ?: "",
          message = "님이 입장하였습니다.",
          type = SYSTEM,
          createdAt = Instant.now().epochSecond
        )
        EXIT -> Chat(
          loungeId = loungeId,
          id = UUID.randomUUID().toString(),
          userId = user?.id ?: member?.userId ?: "",
          userName = user?.name ?: member?.userName ?: "",
          userProfile = user?.profileImage ?: member?.userProfile ?: "",
          message = "님이 퇴장하였습니다.",
          type = SYSTEM,
          createdAt = Instant.now().epochSecond
        )
        EXILE -> Chat(
          loungeId = loungeId,
          id = UUID.randomUUID().toString(),
          userId = user?.id ?: member?.userId ?: "",
          userName = user?.name ?: member?.userName ?: "",
          userProfile = user?.profileImage ?: member?.userProfile ?: "",
          message = "님이 추방되었습니다.",
          type = SYSTEM,
          createdAt = Instant.now().epochSecond
        )
        SETTING_TIME_LIMIT -> Chat(
          loungeId = loungeId,
          id = UUID.randomUUID().toString(),
          userId = "",
          userName = "",
          userProfile = "",
          message = "투표 시간 제한이 ${if (timeLimit != null) "${timeLimit}초" else "무제한으"}로 변경되었습니다.",
          type = SYSTEM,
          createdAt = Instant.now().epochSecond
        )
        SETTING_MAX_MEMBERS -> Chat(
          loungeId = loungeId,
          id = UUID.randomUUID().toString(),
          userId = "",
          userName = "",
          userProfile = "",
          message = "최대 인원이 ${maxMembers}명으로 변경되었습니다.",
          type = SYSTEM,
          createdAt = Instant.now().epochSecond
        )
        SETTING_SECOND_VOTE_CANDIDATES -> Chat(
          loungeId = loungeId,
          id = UUID.randomUUID().toString(),
          userId = "",
          userName = "",
          userProfile = "",
          message = "2차 투표 후보 수가 ${secondVoteCandidates}개로 변경되었습니다.",
          type = SYSTEM,
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
          type = SYSTEM,
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
          type = SYSTEM,
          createdAt = Instant.now().epochSecond
        )
        else -> throw IllegalArgumentException("지원하지 않는 메세지 타입입니다.")
      }
    }

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
