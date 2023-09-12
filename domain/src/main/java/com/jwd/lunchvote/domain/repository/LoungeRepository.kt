package com.jwd.lunchvote.domain.repository

import com.jwd.lunchvote.domain.entity.LoungeChat
import com.jwd.lunchvote.domain.entity.Member
import com.jwd.lunchvote.domain.entity.type.MemberStatusType
import kotlinx.coroutines.flow.Flow

interface LoungeRepository {
    suspend fun checkLoungeExist(loungeId: String) : Boolean
    suspend fun createLounge() : String
    suspend fun joinLounge(loungeId: String)
    fun getMemberList(loungeId: String) : Flow<List<Member>>
    fun getChatList(loungeId: String) : Flow<List<LoungeChat>>
    suspend fun sendChat(loungeId: String, content: String)
    suspend fun updateReady(uid: String, loungeId: String)
    suspend fun exitLounge(uid: String, loungeId: String)
    suspend fun exileMember(memberId: String, loungeId: String)
    fun getMemberStatus(uid: String, loungeId: String) : Flow<MemberStatusType>
}