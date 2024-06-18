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
) : ChatDataSource {

  companion object {
    private const val REFERENCE_CHAT = "Chat"

    private const val COLUMN_USER_ID = "userId"
    private const val COLUMN_USER_NAME = "userName"
    private const val COLUMN_USER_PROFILE = "userProfile"
    private const val COLUMN_MESSAGE = "message"
    private const val COLUMN_TYPE = "type"
    private const val COLUMN_CREATED_AT = "createdAt"
  }

  override fun getChatListFlow(
    loungeId: String
  ): Flow<List<ChatData>> =
    database
      .getReference(REFERENCE_CHAT)
      .child(loungeId)
      .getValueEventFlow<ChatRemote>()
      .map {
        it.mapNotNull { (id, chat) -> chat?.asData(loungeId, id) }
          .sortedByDescending { chat -> chat.createdAt }
      }

  override suspend fun sendChat(
    chat: ChatData
  ) {
    database
      .getReference(REFERENCE_CHAT)
      .child(chat.loungeId)
      .child(chat.id)
      .setValue(chat.asRemote())
      .await()
  }
}