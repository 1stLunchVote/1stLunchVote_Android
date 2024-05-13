package com.jwd.lunchvote.domain.usecase

import com.jwd.lunchvote.domain.entity.LoungeChat
import com.jwd.lunchvote.domain.repository.LoungeRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SendChatUseCase @Inject constructor(
  private val loungeRepository: LoungeRepository
) {
  suspend operator fun invoke(chat: LoungeChat) =
    loungeRepository.sendChat(chat)
}