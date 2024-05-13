package com.jwd.lunchvote.data.source.remote

import com.jwd.lunchvote.data.model.LoungeChatData
import com.jwd.lunchvote.data.model.LoungeData
import com.jwd.lunchvote.data.model.MemberData
import com.jwd.lunchvote.data.model.UserData
import com.jwd.lunchvote.data.model.type.LoungeStatusDataType
import com.jwd.lunchvote.data.model.type.MemberStatusDataType
import kotlinx.coroutines.flow.Flow

interface LoungeDataSource {
  suspend fun checkLoungeExist(loungeId: String): Boolean
  suspend fun createLounge(owner: UserData): String
  suspend fun getLoungeById(id: String): LoungeData
  suspend fun joinLounge(user: UserData, loungeId: String): LoungeData
  fun getLoungeStatus(loungeId: String): Flow<LoungeStatusDataType>
  fun getMemberList(loungeId: String): Flow<List<MemberData>>
  fun getChatList(loungeId: String): Flow<List<LoungeChatData>>
  suspend fun sendChat(chat: LoungeChatData)
  suspend fun updateReady(member: MemberData)
  suspend fun exitLounge(member: MemberData)
  suspend fun exileMember(member: MemberData)
  fun getMemberStatus(member: MemberData) : Flow<MemberStatusDataType>
}