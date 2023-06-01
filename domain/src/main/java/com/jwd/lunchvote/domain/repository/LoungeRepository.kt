package com.jwd.lunchvote.domain.repository

import com.jwd.lunchvote.domain.entity.Member
import kotlinx.coroutines.flow.Flow

interface LoungeRepository {
    fun createLounge() : Flow<String>
    fun getMemberList(loungeId: String) : Flow<List<Member>>
}