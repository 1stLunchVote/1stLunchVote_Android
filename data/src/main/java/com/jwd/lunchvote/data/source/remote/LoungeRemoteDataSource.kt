package com.jwd.lunchvote.data.source.remote

import com.jwd.lunchvote.data.model.LoungeChatData
import com.jwd.lunchvote.data.model.type.MessageDataType
import com.jwd.lunchvote.domain.entity.Member
import kotlinx.coroutines.flow.Flow

interface LoungeRemoteDataSource {
    suspend fun checkLoungeExist(loungeId: String) : Boolean
    suspend fun createLounge() : String
    suspend fun joinLounge(loungeId: String)
    fun getMemberList(loungeId: String) : Flow<List<Member>>
    fun getChatList(loungeId: String) : Flow<List<LoungeChatData>>
    suspend fun sendChat(id: String, loungeId: String, content: String?, messageType: MessageDataType)
    fun updateReady(uid: String, loungeId: String) : Flow<Unit>
    suspend fun exitLounge(uid: String, loungeId: String)
}