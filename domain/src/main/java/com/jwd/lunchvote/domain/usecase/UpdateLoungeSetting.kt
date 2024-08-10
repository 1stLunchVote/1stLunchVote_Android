package com.jwd.lunchvote.domain.usecase

import com.jwd.lunchvote.domain.entity.Chat
import com.jwd.lunchvote.domain.entity.Chat.SystemMessageType.SETTING_MAX_MEMBERS
import com.jwd.lunchvote.domain.entity.Chat.SystemMessageType.SETTING_MIN_DISLIKE_FOODS
import com.jwd.lunchvote.domain.entity.Chat.SystemMessageType.SETTING_MIN_LIKE_FOODS
import com.jwd.lunchvote.domain.entity.Chat.SystemMessageType.SETTING_SECOND_VOTE_CANDIDATES
import com.jwd.lunchvote.domain.entity.Chat.SystemMessageType.SETTING_TIME_LIMIT
import com.jwd.lunchvote.domain.entity.Member
import com.jwd.lunchvote.domain.repository.ChatRepository
import com.jwd.lunchvote.domain.repository.LoungeRepository
import com.jwd.lunchvote.domain.repository.MemberRepository
import kotlinx.coroutines.flow.first
import kr.co.inbody.config.config.VoteConfig
import kr.co.inbody.config.error.LoungeError
import javax.inject.Inject

class UpdateLoungeSetting @Inject constructor(
  private val loungeRepository: LoungeRepository,
  private val memberRepository: MemberRepository,
  private val chatRepository: ChatRepository
) {

  suspend operator fun invoke(
    loungeId: String,
    timeLimit: Int? = null,
    maxMembers: Int? = null,
    secondVoteCandidates: Int? = null,
    minLikeFoods: Int? = null,
    minDislikeFoods: Int? = null
  ) {
    val lounge = loungeRepository.getLoungeById(loungeId)
    if (lounge.members > (maxMembers ?: VoteConfig.DEFAULT_MAX_MEMBERS)) {
      throw LoungeError.LoungeSettingFailed
    }

    val updatedLounge = lounge.copy(
      timeLimit = timeLimit ?: lounge.timeLimit,
      maxMembers = maxMembers ?: lounge.maxMembers,
      secondVoteCandidates = secondVoteCandidates ?: lounge.secondVoteCandidates,
      minLikeFoods = minLikeFoods ?: lounge.minLikeFoods,
      minDislikeFoods = minDislikeFoods ?: lounge.minDislikeFoods
    )

    loungeRepository.updateLounge(updatedLounge)

    val timeLimitMap = mapOf(10 to "10초", 20 to "20초", 30 to "30초", 60 to "1분", 90 to "1분 30초", 120 to "2분")

    val chat = Chat.builder()
      .loungeId(loungeId)
      .let {
        when {
          timeLimit != null -> it.type(SETTING_TIME_LIMIT).arg(timeLimitMap[timeLimit] ?: "${timeLimit}초")
          maxMembers != null -> it.type(SETTING_MAX_MEMBERS).arg("${maxMembers}명")
          secondVoteCandidates != null -> it.type(SETTING_SECOND_VOTE_CANDIDATES).arg("${secondVoteCandidates}개")
          minLikeFoods != null -> it.type(SETTING_MIN_LIKE_FOODS).arg("${minLikeFoods}개")
          minDislikeFoods != null -> it.type(SETTING_MIN_DISLIKE_FOODS).arg("${minDislikeFoods}개")
          else -> throw LoungeError.LoungeSettingFailed
        }
      }
      .build()
    chatRepository.sendChat(chat)
  }
}