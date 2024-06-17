package com.jwd.lunchvote.domain.repository

import com.jwd.lunchvote.domain.entity.Member
import kotlinx.coroutines.flow.Flow

interface MemberRepository {

  suspend fun createMember(member: Member)
  fun getMemberListFlow(loungeId: String): Flow<List<Member>>
  fun getMemberTypeFlow(loungeId: String, userId: String): Flow<Member.Type>
  suspend fun getMemberByUserId(userId: String, loungeId: String): Member
  suspend fun updateMemberReadyType(member: Member)
  suspend fun updateMemberStatus(member: Member, status: Member.Status)
  suspend fun updateMembersStatusByLoungeId(loungeId: String, status: Member.Status)
  suspend fun exileMember(member: Member)
  suspend fun deleteMember(member: Member)
}