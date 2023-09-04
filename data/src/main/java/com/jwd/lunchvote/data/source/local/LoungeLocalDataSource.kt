package com.jwd.lunchvote.data.source.local

import com.jwd.lunchvote.data.model.LoungeChatData
import com.jwd.lunchvote.data.model.type.MessageDataType
import com.jwd.lunchvote.domain.entity.Member
import kotlinx.coroutines.flow.Flow

interface LoungeLocalDataSource {
    fun getChatList(loungeId: String) : Flow<List<LoungeChatData>>
    suspend fun putChatList(chatList: List<LoungeChatData>, loungeId: String)
    fun getMemberList(loungeId: String): Flow<List<Member>>
    suspend fun putMemberList(memberList: List<Member>, loungeId: String)
    suspend fun insertChat(id: String, loungeId: String, content: String, type: MessageDataType)
    suspend fun deleteChat(loungeId: String)
    suspend fun updateMemberReady(uid: String, loungeId: String)
    suspend fun deleteAllChat(loungeId: String)
}