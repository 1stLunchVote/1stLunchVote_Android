package com.jwd.lunchvote.domain.usecase

import com.jwd.lunchvote.domain.entity.Member
import com.jwd.lunchvote.domain.repository.LoungeRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExitLoungeUseCase @Inject constructor(
  private val loungeRepository: LoungeRepository
) {
  suspend operator fun invoke(member: Member) =
      loungeRepository.exitLounge(member)
}