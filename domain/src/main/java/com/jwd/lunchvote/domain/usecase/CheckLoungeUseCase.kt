package com.jwd.lunchvote.domain.usecase

import com.jwd.lunchvote.domain.repository.LoungeRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CheckLoungeUseCase @Inject constructor(
  private val loungeRepository: LoungeRepository
) {
  suspend operator fun invoke(loungeId: String): Boolean =
    loungeRepository.checkLoungeExist(loungeId)
}