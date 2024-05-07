package com.jwd.lunchvote.domain.usecase

import com.jwd.lunchvote.domain.entity.User
import com.jwd.lunchvote.domain.repository.LoungeRepository
import javax.inject.Inject

class CreateLoungeUseCase @Inject constructor(
  private val loungeRepository: LoungeRepository
) {
  suspend operator fun invoke(user: User): String =
    loungeRepository.createLounge(user)
}