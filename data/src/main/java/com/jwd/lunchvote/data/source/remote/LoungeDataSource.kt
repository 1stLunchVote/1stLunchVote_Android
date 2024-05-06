package com.jwd.lunchvote.data.source.remote

import com.jwd.lunchvote.data.model.LoungeChatData
import com.jwd.lunchvote.data.model.MemberData
import com.jwd.lunchvote.data.model.UserData
import com.jwd.lunchvote.data.model.type.LoungeStatusDataType
import com.jwd.lunchvote.data.model.type.MessageDataType
import com.jwd.lunchvote.domain.entity.User
import kotlinx.coroutines.flow.Flow

interface LoungeDataSource {
    suspend fun checkLoungeExist(loungeId: String) : Boolean
    suspend fun createLounge(owner: UserData) : String
    suspend fun joinLounge(user: UserData, loungeId: String)
    fun getMemberList(loungeId: String) : Flow<List<MemberData>>
    fun getChatList(loungeId: String) : Flow<List<LoungeChatData>>
    fun getLoungeStatus(loungeId: String) : Flow<LoungeStatusDataType>
    suspend fun sendChat(id: String, loungeId: String, content: String?, messageType: MessageDataType)
    suspend fun updateReady(uid: String, loungeId: String, isOwner: Boolean)
    suspend fun exitLounge(uid: String, loungeId: String)
    suspend fun exileMember(memberId: String, loungeId: String)
}