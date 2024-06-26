package com.jwd.lunchvote.data.source.local

import com.jwd.lunchvote.data.model.ChatData
import com.jwd.lunchvote.data.model.MemberData
import kotlinx.coroutines.flow.Flow

interface LoungeLocalDataSource {
  fun getChatList(loungeId: String): Flow<List<ChatData>>
  suspend fun putChatList(chatList: List<ChatData>, loungeId: String)
  fun getMemberList(loungeId: String): Flow<List<MemberData>>
  suspend fun putMemberList(memberList: List<MemberData>, loungeId: String)
  suspend fun insertChat(id: String, loungeId: String, content: String, type: ChatData.Type)
  suspend fun deleteChat(loungeId: String)
  suspend fun updateMemberReady(uid: String, loungeId: String)
  suspend fun deleteAllChat(loungeId: String)
}