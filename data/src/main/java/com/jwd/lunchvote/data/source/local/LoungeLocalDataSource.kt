package com.jwd.lunchvote.data.source.local

import com.jwd.lunchvote.data.model.LoungeChatData
import com.jwd.lunchvote.data.model.MemberData
import com.jwd.lunchvote.data.model.type.MessageDataType
import com.jwd.lunchvote.domain.entity.Member
import kotlinx.coroutines.flow.Flow

interface LoungeLocalDataSource {
    fun getChatList(loungeId: String) : Flow<List<LoungeChatData>>
    suspend fun putChatList(chatList: List<LoungeChatData>, loungeId: String)
    fun getMemberList(loungeId: String): Flow<List<MemberData>>
    suspend fun putMemberList(memberList: List<MemberData>, loungeId: String)
    suspend fun insertChat(id: String, loungeId: String, content: String, type: MessageDataType)
    suspend fun deleteChat(loungeId: String)
    suspend fun updateMemberReady(uid: String, loungeId: String)
    suspend fun deleteAllChat(loungeId: String)
    suspend fun updateCurrentLounge(loungeId: String)
    suspend fun deleteCurrentLounge()
    fun getCurrentLounge(): Flow<String?>
}