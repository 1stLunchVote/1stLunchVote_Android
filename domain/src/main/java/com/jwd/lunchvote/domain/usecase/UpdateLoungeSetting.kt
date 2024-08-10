package com.jwd.lunchvote.domain.usecase

import com.jwd.lunchvote.domain.entity.Chat
import com.jwd.lunchvote.domain.entity.Chat.SystemMessageType.SETTING_MAX_MEMBERS
import com.jwd.lunchvote.domain.entity.Chat.SystemMessageType.SETTING_MIN_DISLIKE_FOODS
import com.jwd.lunchvote.domain.entity.Chat.SystemMessageType.SETTING_MIN_LIKE_FOODS
import com.jwd.lunchvote.domain.entity.Chat.SystemMessageType.SETTING_SECOND_VOTE_CANDIDATES
import com.jwd.lunchvote.domain.entity.Chat.SystemMessageType.SETTING_TIME_LIMIT
import com.jwd.lunchvote.domain.repository.ChatRepository
import com.jwd.lunchvote.domain.repository.LoungeRepository
import kr.co.inbody.config.config.VoteConfig
import kr.co.inbody.config.error.LoungeError
import javax.inject.Inject

class UpdateLoungeSetting @Inject constructor(
  private val loungeRepository: LoungeRepository,
  private val chatRepository: ChatRepository
) {

  suspend operator fun invoke(
    loungeId: String, 
    timeLimit: Int? = NO_VALUE,
    maxMembers: Int? = NO_VALUE,
    secondVoteCandidates: Int? = NO_VALUE,
    minLikeFoods: Int? = NO_VALUE,
    minDislikeFoods: Int? = NO_VALUE
  ) {
    val lounge = loungeRepository.getLoungeById(loungeId)
    
    when {
      timeLimit != NO_VALUE -> {
        if (timeLimit == null) {
          loungeRepository.updateLounge(
            lounge = lounge.copy(
              timeLimit = null,
              minLikeFoods = VoteConfig.DEFAULT_MIN_LIKE_FOODS,
              minDislikeFoods = VoteConfig.DEFAULT_MIN_DISLIKE_FOODS
            )
          )
          chatRepository.sendChat(
            chat = Chat.builder(loungeId)
              .type(SETTING_TIME_LIMIT)
              .timeLimit(null)
              .build()
          )
        } else {
          loungeRepository.updateLounge(
            lounge = lounge.copy(
              timeLimit = timeLimit,
              minLikeFoods = null,
              minDislikeFoods = null
            )
          )
          chatRepository.sendChat(
            chat = Chat.builder(loungeId)
              .type(SETTING_TIME_LIMIT)
              .timeLimit(timeLimit)
              .build()
          )
        }
      }
      maxMembers != NO_VALUE -> {
        if (lounge.members > (maxMembers ?: VoteConfig.DEFAULT_MAX_MEMBERS)) {
          throw LoungeError.LoungeSettingFailed
        }

        loungeRepository.updateLounge(
          lounge = lounge.copy(
            maxMembers = maxMembers ?: VoteConfig.DEFAULT_MAX_MEMBERS
          )
        )

        chatRepository.sendChat(
          chat = Chat.builder(loungeId)
            .type(SETTING_MAX_MEMBERS)
            .maxMembers(maxMembers ?: VoteConfig.DEFAULT_MAX_MEMBERS)
            .build()
        )
      }
      secondVoteCandidates != NO_VALUE -> {
        loungeRepository.updateLounge(
          lounge = lounge.copy(
            secondVoteCandidates = secondVoteCandidates ?: VoteConfig.DEFAULT_SECOND_VOTE_CANDIDATES
          )
        )

        chatRepository.sendChat(
          chat = Chat.builder(loungeId)
            .type(SETTING_SECOND_VOTE_CANDIDATES)
            .secondVoteCandidates(secondVoteCandidates ?: VoteConfig.DEFAULT_SECOND_VOTE_CANDIDATES)
            .build()
        )
      }
      minLikeFoods != NO_VALUE -> {
        loungeRepository.updateLounge(
          lounge = lounge.copy(
            minLikeFoods = minLikeFoods
          )
        )

        chatRepository.sendChat(
          chat = Chat.builder(loungeId)
            .type(SETTING_MIN_LIKE_FOODS)
            .minLikeFoods(minLikeFoods)
            .build()
        )
      }
      minDislikeFoods != NO_VALUE -> {
        loungeRepository.updateLounge(
          lounge = lounge.copy(
            minDislikeFoods = minDislikeFoods
          )
        )

        chatRepository.sendChat(
          chat = Chat.builder(loungeId)
            .type(SETTING_MIN_DISLIKE_FOODS)
            .minDislikeFoods(minDislikeFoods)
            .build()
        )
      }
      else -> throw LoungeError.LoungeSettingFailed
    }
  }
  
  companion object {
    const val NO_VALUE = -1
  }
}