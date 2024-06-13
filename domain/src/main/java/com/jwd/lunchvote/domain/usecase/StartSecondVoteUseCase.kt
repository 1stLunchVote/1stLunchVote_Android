package com.jwd.lunchvote.domain.usecase

import com.jwd.lunchvote.domain.entity.Lounge
import com.jwd.lunchvote.domain.entity.Member
import com.jwd.lunchvote.domain.repository.LoungeRepository
import com.jwd.lunchvote.domain.repository.MemberRepository
import com.jwd.lunchvote.domain.repository.SecondVoteRepository
import javax.inject.Inject

class StartSecondVoteUseCase @Inject constructor(
  private val loungeRepository: LoungeRepository,
  private val memberRepository: MemberRepository,
  private val secondVoteRepository: SecondVoteRepository
) {

  suspend operator fun invoke(loungeId: String, selectedFoodIds: List<String>) {
    secondVoteRepository.createSecondVote(loungeId, selectedFoodIds)

    memberRepository.updateMembersStatusByLoungeId(loungeId, Member.Status.VOTING)

    loungeRepository.updateLoungeStatusById(loungeId, Lounge.Status.SECOND_VOTE)
  }
}