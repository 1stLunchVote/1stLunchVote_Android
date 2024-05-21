package com.jwd.lunchvote.domain.repository

import com.jwd.lunchvote.domain.entity.Chat
import kotlinx.coroutines.flow.Flow

interface ChatRepository {

  suspend fun sendChat(chat: Chat)
  fun getChatListFlow(loungeId: String): Flow<List<Chat>>
}