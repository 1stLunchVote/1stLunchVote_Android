package com.jwd.lunchvote.data.repository

import com.jwd.lunchvote.data.mapper.asData
import com.jwd.lunchvote.data.mapper.asDomain
import com.jwd.lunchvote.data.source.remote.MemberDataSource
import com.jwd.lunchvote.domain.entity.Member
import com.jwd.lunchvote.domain.repository.MemberRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class MemberRepositoryImpl @Inject constructor(
  private val memberDataSource: MemberDataSource
): MemberRepository {

  override suspend fun createMember(member: Member) {
    memberDataSource.createMember(member.asData())
  }

  override fun getMemberListFlow(loungeId: String): Flow<List<Member>> =
    memberDataSource.getMemberListFlow(loungeId).map { list -> list.map { it.asDomain() } }

  override fun getMemberTypeFlow(loungeId: String, userId: String): Flow<Member.Type> =
    memberDataSource.getMemberTypeFlow(loungeId, userId).map { it.asDomain() }

  override suspend fun getMemberByUserId(userId: String, loungeId: String): Member? =
    memberDataSource.getMemberByUserId(userId, loungeId)?.asDomain()

  override suspend fun updateMemberReadyType(member: Member) {
    memberDataSource.updateMemberReadyType(member.asData())
  }

  override suspend fun updateMemberType(member: Member, type: Member.Type) {
    memberDataSource.updateMemberType(member.asData(), type.asData())
  }

  override suspend fun updateMemberStatus(member: Member, status: Member.Status) {
    memberDataSource.updateMemberStatus(member.asData(), status.asData())
  }

  override suspend fun updateMembersStatusByLoungeId(loungeId: String, status: Member.Status) {
    memberDataSource.updateMembersStatusByLoungeId(loungeId, status.asData())
  }

  override suspend fun exileMember(member: Member) {
    memberDataSource.exileMember(member.asData())
  }

  override suspend fun deleteMember(member: Member) {
    memberDataSource.deleteMember(member.asData())
  }
}