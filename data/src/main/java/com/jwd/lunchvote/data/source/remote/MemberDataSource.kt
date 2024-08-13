package com.jwd.lunchvote.data.source.remote

import com.jwd.lunchvote.data.model.MemberData
import kotlinx.coroutines.flow.Flow

interface MemberDataSource {

  suspend fun createMember(member: MemberData)
  fun getMemberListFlow(loungeId: String): Flow<List<MemberData>>
  fun getMemberTypeFlow(loungeId: String, userId: String): Flow<MemberData.Type>
  suspend fun getMember(userId: String, loungeId: String): MemberData?
  suspend fun updateMemberReadyType(member: MemberData)
  suspend fun updateMemberType(member: MemberData, type: MemberData.Type)
  suspend fun updateMemberStatus(member: MemberData, status: MemberData.Status)
  suspend fun updateMembersStatusByLoungeId(loungeId: String, status: MemberData.Status)
  suspend fun exileMember(member: MemberData)
  suspend fun deleteMember(member: MemberData)
}