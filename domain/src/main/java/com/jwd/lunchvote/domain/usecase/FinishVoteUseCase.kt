package com.jwd.lunchvote.domain.usecase

import com.jwd.lunchvote.domain.entity.Lounge
import com.jwd.lunchvote.domain.entity.VoteResult
import com.jwd.lunchvote.domain.repository.LoungeRepository
import com.jwd.lunchvote.domain.repository.VoteResultRepository
import javax.inject.Inject

class FinishVoteUseCase @Inject constructor(
  private val loungeRepository: LoungeRepository,
  private val voteResultRepository: VoteResultRepository
) {

  suspend operator fun invoke(loungeId: String, foodId: String, voteRatio: Float) {
    val voteResult = VoteResult(
      loungeId = loungeId,
      foodId = foodId,
      voteRatio = voteRatio
    )
    voteResultRepository.saveVoteResult(voteResult)

    loungeRepository.updateLoungeStatusById(loungeId, Lounge.Status.FINISHED)
  }
}