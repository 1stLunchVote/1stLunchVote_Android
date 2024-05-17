package com.jwd.lunchvote.domain.usecase

import com.jwd.lunchvote.domain.repository.LoungeRepository
import javax.inject.Inject

class GetMemberByUserIdUseCase @Inject constructor(
  private val loungeRepository: LoungeRepository
) {
  suspend operator fun invoke(userId: String, loungeId: String) =
    loungeRepository.getMemberByUserId(userId, loungeId)
}