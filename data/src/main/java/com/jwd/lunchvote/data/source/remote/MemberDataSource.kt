package com.jwd.lunchvote.data.source.remote

import com.jwd.lunchvote.data.model.MemberData
import kotlinx.coroutines.flow.Flow

interface MemberDataSource {

  suspend fun createMember(member: MemberData)
  fun getMemberListFlow(loungeId: String): Flow<List<MemberData>>
  fun getMemberTypeFlow(member: MemberData): Flow<MemberData.Type>
  suspend fun getMemberByUserId(userId: String, loungeId: String): MemberData
  suspend fun updateMemberReadyType(member: MemberData)
  suspend fun exileMember(member: MemberData)
  suspend fun deleteMember(member: MemberData)
}