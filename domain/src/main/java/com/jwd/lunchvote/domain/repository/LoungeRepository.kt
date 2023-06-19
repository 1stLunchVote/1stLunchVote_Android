package com.jwd.lunchvote.domain.repository

import com.jwd.lunchvote.domain.entity.LoungeChat
import com.jwd.lunchvote.domain.entity.Member
import com.jwd.lunchvote.domain.entity.MemberStatus
import kotlinx.coroutines.flow.Flow

interface LoungeRepository {
    fun checkLoungeExist(loungeId: String) : Flow<Boolean>
    fun createLounge() : Flow<String>
    fun joinLounge(loungeId: String) : Flow<Unit>
    fun getMemberList(loungeId: String) : Flow<List<Member>>
    fun getChatList(loungeId: String) : Flow<List<LoungeChat>>
    fun sendChat(loungeId: String, content: String) : Flow<Unit>
    fun updateReady(uid: String, loungeId: String) : Flow<Unit>
    fun exitLounge(uid: String, loungeId: String) : Flow<Unit>
    fun getMemberStatus(uid: String, loungeId: String) : Flow<MemberStatus>
}