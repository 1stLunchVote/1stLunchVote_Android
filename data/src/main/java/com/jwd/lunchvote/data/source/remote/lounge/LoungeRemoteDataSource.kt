package com.jwd.lunchvote.data.source.remote.lounge

import com.jwd.lunchvote.domain.entity.Member
import kotlinx.coroutines.flow.Flow

interface LoungeRemoteDataSource {
    fun createLounge() : Flow<String?>
    fun getMemberList(loungeId: String) : Flow<List<Member>>
}