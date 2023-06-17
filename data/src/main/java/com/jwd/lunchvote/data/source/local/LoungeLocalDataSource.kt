package com.jwd.lunchvote.data.source.local

import com.jwd.lunchvote.domain.entity.LoungeChat
import com.jwd.lunchvote.domain.entity.Member
import kotlinx.coroutines.flow.Flow

interface LoungeLocalDataSource {
    fun getChatList(loungeId: String) : Flow<List<LoungeChat>>
    suspend fun putChatList(chatList: List<LoungeChat>, loungeId: String)
    fun getMemberList(loungeId: String): Flow<List<Member>>
    suspend fun putMemberList(memberList: List<Member>, loungeId: String)
    fun insertChat(loungeId: String, content: String, type: Int): Flow<Unit>
    fun deleteChat(loungeId: String): Flow<Unit>
    fun updateMemberReady(uid: String, loungeId: String): Flow<Unit>
}