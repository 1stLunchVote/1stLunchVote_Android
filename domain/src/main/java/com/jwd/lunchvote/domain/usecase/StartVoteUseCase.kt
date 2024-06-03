package com.jwd.lunchvote.domain.usecase

import com.jwd.lunchvote.domain.entity.Member
import com.jwd.lunchvote.domain.repository.LoungeRepository
import com.jwd.lunchvote.domain.repository.MemberRepository
import javax.inject.Inject

class StartVoteUseCase @Inject constructor(
  private val loungeRepository: LoungeRepository,
  private val memberRepository: MemberRepository
) {

  suspend operator fun invoke(id: String) {
    val memberList = memberRepository.updateMembersStatusByLoungeId(id, Member.Status.VOTING)

    loungeRepository.startLoungeById(id)

  }
}