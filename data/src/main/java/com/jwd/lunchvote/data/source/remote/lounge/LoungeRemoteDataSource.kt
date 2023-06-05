package com.jwd.lunchvote.data.source.remote.lounge

import com.jwd.lunchvote.domain.entity.LoungeChat
import com.jwd.lunchvote.domain.entity.Member
import kotlinx.coroutines.flow.Flow

interface LoungeRemoteDataSource {
    fun createLounge() : Flow<String?>
    fun joinLounge(loungeId: String) : Flow<Unit>
    fun getMemberList(loungeId: String) : Flow<List<Member>>
    fun getChatList(loungeId: String) : Flow<List<LoungeChat>>
}