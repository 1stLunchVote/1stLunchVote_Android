package com.jwd.lunchvote.domain.repository

import com.jwd.lunchvote.domain.entity.Chat
import com.jwd.lunchvote.domain.entity.Lounge
import com.jwd.lunchvote.domain.entity.Member
import com.jwd.lunchvote.domain.entity.User
import com.jwd.lunchvote.domain.entity.type.LoungeStatus
import kotlinx.coroutines.flow.Flow

interface LoungeRepository {

  suspend fun checkLoungeExist(loungeId: String): Boolean
  suspend fun createLounge(owner: User): String
  suspend fun getLoungeById(id: String): Lounge
  suspend fun joinLounge(user: User, loungeId: String): Lounge
  fun getLoungeStatus(loungeId: String): Flow<LoungeStatus>
  fun getMemberList(loungeId: String): Flow<List<Member>>
  fun getChatList(loungeId: String): Flow<List<Chat>>
  suspend fun sendChat(chat: Chat)
  suspend fun updateReady(member: Member)
  suspend fun exitLounge(member: Member)
  suspend fun exileMember(member: Member)
  fun getMemberStatus(member: Member): Flow<Member.Type>
  suspend fun getMemberByUserId(userId: String, loungeId: String): Member
}