package com.jwd.lunchvote.domain.usecase

import com.jwd.lunchvote.domain.entity.Lounge
import com.jwd.lunchvote.domain.repository.LoungeRepository
import javax.inject.Inject

class FinishVote @Inject constructor(
  private val loungeRepository: LoungeRepository
) {

  suspend operator fun invoke(loungeId: String) {
    loungeRepository.updateLoungeStatusById(loungeId, Lounge.Status.FINISHED)
  }
}