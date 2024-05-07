package com.jwd.lunchvote.domain.usecase

import com.jwd.lunchvote.domain.entity.LoungeChat
import com.jwd.lunchvote.domain.repository.LoungeRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetChatListUseCase @Inject constructor(
  private val loungeRepository: LoungeRepository
) {
  operator fun invoke(loungeId: String): Flow<List<LoungeChat>> =
    loungeRepository.getChatList(loungeId)
}