package com.jwd.lunchvote.domain.usecase

import com.jwd.lunchvote.domain.entity.Chat
import com.jwd.lunchvote.domain.repository.ChatRepository
import com.jwd.lunchvote.domain.repository.LoungeRepository
import kr.co.inbody.config.config.VoteConfig.DEFAULT_MIN_DISLIKE_FOODS
import kr.co.inbody.config.config.VoteConfig.DEFAULT_MIN_LIKE_FOODS
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
              minLikeFoods = DEFAULT_MIN_LIKE_FOODS,
              minDislikeFoods = DEFAULT_MIN_DISLIKE_FOODS
            )
          )
          chatRepository.sendChat(
            chat = Chat.Builder(loungeId)
              .setTimeLimit(null)
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
            chat = Chat.Builder(loungeId)
              .setTimeLimit(timeLimit)
              .build()
          )
        }
      }
      maxMembers != NO_VALUE && maxMembers != null -> {
        if (lounge.members > (maxMembers)) {
          throw LoungeError.LoungeSettingFailed
        }

        loungeRepository.updateLounge(
          lounge = lounge.copy(maxMembers = maxMembers)
        )

        chatRepository.sendChat(
          chat = Chat.Builder(loungeId)
            .setMaxMembers(maxMembers)
            .build()
        )
      }
      secondVoteCandidates != NO_VALUE && secondVoteCandidates != null-> {
        loungeRepository.updateLounge(
          lounge = lounge.copy(secondVoteCandidates = secondVoteCandidates)
        )

        chatRepository.sendChat(
          chat = Chat.Builder(loungeId)
            .setSecondVoteCandidates(secondVoteCandidates)
            .build()
        )
      }
      minLikeFoods != NO_VALUE -> {
        loungeRepository.updateLounge(
          lounge = lounge.copy(minLikeFoods = minLikeFoods)
        )

        chatRepository.sendChat(
          chat = Chat.Builder(loungeId)
            .setMinLikeFoods(minLikeFoods)
            .build()
        )
      }
      minDislikeFoods != NO_VALUE -> {
        loungeRepository.updateLounge(
          lounge = lounge.copy(minDislikeFoods = minDislikeFoods)
        )

        chatRepository.sendChat(
          chat = Chat.Builder(loungeId)
            .setMinDislikeFoods(minDislikeFoods)
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