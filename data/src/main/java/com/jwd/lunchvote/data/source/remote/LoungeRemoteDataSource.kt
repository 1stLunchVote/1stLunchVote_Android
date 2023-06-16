package com.jwd.lunchvote.data.source.remote

import com.jwd.lunchvote.domain.entity.LoungeChat
import com.jwd.lunchvote.domain.entity.Member
import kotlinx.coroutines.flow.Flow

interface LoungeRemoteDataSource {
    fun createLounge() : Flow<String?>
    fun joinLounge(loungeId: String) : Flow<Unit?>
    fun getMemberList(loungeId: String) : Flow<List<Member>>
    fun getChatList(loungeId: String) : Flow<List<LoungeChat>>
    fun sendChat(loungeId: String, content: String?, messageType: Int = 0) : Flow<Unit>
    fun updateReady(uid: String, loungeId: String) : Flow<Unit>
    fun exitLounge(uid: String, loungeId: String) : Flow<Unit>
}