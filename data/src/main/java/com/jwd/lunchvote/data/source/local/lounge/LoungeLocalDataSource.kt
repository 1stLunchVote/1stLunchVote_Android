package com.jwd.lunchvote.data.source.local.lounge

import com.jwd.lunchvote.data.room.entity.ChatEntity
import com.jwd.lunchvote.data.room.entity.MemberEntity
import com.jwd.lunchvote.domain.entity.LoungeChat
import com.jwd.lunchvote.domain.entity.Member
import kotlinx.coroutines.flow.Flow

interface LoungeLocalDataSource {
    fun getChatList(loungeId: String) : Flow<List<ChatEntity>>
    suspend fun putChatList(chatList: List<LoungeChat>, loungeId: String)
    fun getMemberList(loungeId: String): Flow<List<MemberEntity>>
    suspend fun putMemberList(memberList: List<Member>, loungeId: String)
    fun insertChat(loungeId: String, content: String, type: Int): Flow<Unit>
    fun deleteChat(loungeId: String): Flow<Unit>
}