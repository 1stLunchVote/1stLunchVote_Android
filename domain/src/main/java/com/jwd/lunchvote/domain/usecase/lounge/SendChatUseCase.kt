package com.jwd.lunchvote.domain.usecase.lounge

import com.jwd.lunchvote.domain.repository.LoungeRepository
import javax.inject.Inject

class SendChatUseCase @Inject constructor(
    private val repository: LoungeRepository
) {
    operator fun invoke(loungeId: String, content: String) = repository.sendChat(loungeId, content)
}