package com.jwd.lunchvote.data.repository

import com.jwd.lunchvote.data.model.MemberData
import com.jwd.lunchvote.domain.entity.Member
import com.jwd.lunchvote.domain.entity.User
import com.jwd.lunchvote.domain.repository.MemberRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class MemberRepositoryImpl @Inject constructor(
  private val memberRepository: MemberRepository
): MemberRepository {

  override suspend fun createMember(member: Member) {
    memberRepository.createMember(member)
  }

  override fun getMemberListFlow(loungeId: String): Flow<List<Member>> =
    memberRepository.getMemberListFlow(loungeId)

  override fun getMemberTypeFlow(member: Member): Flow<Member.Type> =
    memberRepository.getMemberTypeFlow(member)

  override suspend fun getMemberByUserId(userId: String, loungeId: String): Member =
    memberRepository.getMemberByUserId(userId, loungeId)

  override suspend fun updateMemberReadyType(member: Member) {
    memberRepository.updateMemberReadyType(member)
  }

  override suspend fun exileMember(member: Member) {
    memberRepository.exileMember(member)
  }

  override suspend fun deleteMember(member: Member) {
    memberRepository.deleteMember(member)
  }
}