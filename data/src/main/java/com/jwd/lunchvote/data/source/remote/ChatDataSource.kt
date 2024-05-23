package com.jwd.lunchvote.data.source.remote

import com.jwd.lunchvote.data.model.ChatData
import kotlinx.coroutines.flow.Flow

interface ChatDataSource {

  suspend fun sendChat(chat: ChatData)
  fun getChatListFlow(loungeId: String): Flow<List<ChatData>>
}