package com.jwd.lunchvote.domain.usecase

import com.jwd.lunchvote.domain.entity.User
import com.jwd.lunchvote.domain.repository.LoungeRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class JoinLoungeUseCase @Inject constructor(
  private val loungeRepository: LoungeRepository
) {
  suspend operator fun invoke(user: User, loungeId: String) =
    loungeRepository.joinLounge(user, loungeId)
}