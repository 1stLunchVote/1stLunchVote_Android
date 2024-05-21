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

  override fun getMemberTypeFlow(member: Member): Flow<Member.Type> =
    memberDataSource.getMemberTypeFlow(member.asData()).map { it.asDomain() }

  override suspend fun getMemberByUserId(userId: String, loungeId: String): Member =
    memberDataSource.getMemberByUserId(userId, loungeId).asDomain()

  override suspend fun updateMemberReadyType(member: Member) {
    memberDataSource.updateMemberReadyType(member.asData())
  }

  override suspend fun exileMember(member: Member) {
    memberDataSource.exileMember(member.asData())
  }

  override suspend fun deleteMember(member: Member) {
    memberDataSource.deleteMember(member.asData())
  }
}