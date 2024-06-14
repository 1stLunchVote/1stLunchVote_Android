package com.jwd.lunchvote.domain.usecase

import com.jwd.lunchvote.domain.entity.Lounge
import com.jwd.lunchvote.domain.entity.Member
import com.jwd.lunchvote.domain.repository.LoungeRepository
import com.jwd.lunchvote.domain.repository.MemberRepository
import javax.inject.Inject

class StartFirstVote @Inject constructor(
  private val loungeRepository: LoungeRepository,
  private val memberRepository: MemberRepository
) {

  suspend operator fun invoke(loungeId: String) {
    memberRepository.updateMembersStatusByLoungeId(loungeId, Member.Status.VOTING)

    loungeRepository.updateLoungeStatusById(loungeId, Lounge.Status.FIRST_VOTE)
  }
}