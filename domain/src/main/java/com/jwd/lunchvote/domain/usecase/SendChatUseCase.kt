package com.jwd.lunchvote.domain.usecase

import com.jwd.lunchvote.domain.repository.LoungeRepository
import javax.inject.Inject

class SendChatUseCase @Inject constructor(
    private val repository: LoungeRepository
) {
    suspend operator fun invoke(loungeId: String, content: String) = repository.sendChat(loungeId, content)
}