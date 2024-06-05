package com.jwd.lunchvote.remote.source

import com.google.firebase.database.FirebaseDatabase
import com.jwd.lunchvote.data.model.ChatData
import com.jwd.lunchvote.data.source.remote.ChatDataSource
import com.jwd.lunchvote.remote.mapper.asData
import com.jwd.lunchvote.remote.mapper.asRemote
import com.jwd.lunchvote.remote.model.ChatRemote
import com.jwd.lunchvote.remote.util.getValueEventFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class ChatDataSourceImpl @Inject constructor(
  private val database: FirebaseDatabase
): ChatDataSource {

  companion object {
    const val CHAT_PATH = "Chat"

    const val CHAT_LOUNGE_ID = "loungeId"
    const val CHAT_USER_ID = "userId"
    const val CHAT_USER_PROFILE = "userProfile"
    const val CHAT_MESSAGE = "message"
    const val CHAT_TYPE = "type"
    const val CHAT_CREATED_AT = "createdAt"
  }

  override fun getChatListFlow(
    loungeId: String
  ): Flow<List<ChatData>> =
    database
      .getReference(CHAT_PATH)
      .child(loungeId)
      .getValueEventFlow<ChatRemote>()
      .map {
        it.mapNotNull { (key, value) -> value?.asData(key) }
          .sortedByDescending { chat -> chat.createdAt }
      }

  override suspend fun sendChat(
    chat: ChatData
  ) {
    database
      .getReference(CHAT_PATH)
      .child(chat.loungeId)
      .child(chat.id)
      .setValue(chat.asRemote())
      .await()
  }
}