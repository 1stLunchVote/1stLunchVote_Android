package com.jwd.lunchvote.data.repository

import com.jwd.lunchvote.data.mapper.asData
import com.jwd.lunchvote.data.mapper.asDomain
import com.jwd.lunchvote.data.source.remote.ChatDataSource
import com.jwd.lunchvote.domain.entity.Chat
import com.jwd.lunchvote.domain.repository.ChatRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ChatRepositoryImpl @Inject constructor(
  private val chatDataSource: ChatDataSource
): ChatRepository {

  override fun getChatListFlow(loungeId: String): Flow<List<Chat>> =
    chatDataSource.getChatListFlow(loungeId).map { list -> list.map { it.asDomain() } }

  override suspend fun sendChat(chat: Chat) {
    chatDataSource.sendChat(chat.asData())
  }
}