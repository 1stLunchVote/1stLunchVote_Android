package com.jwd.lunchvote.domain.usecase

import com.jwd.lunchvote.domain.entity.Member
import com.jwd.lunchvote.domain.repository.LoungeRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExileMemberUseCase @Inject constructor(
  private val loungeRepository: LoungeRepository
) {
  suspend operator fun invoke(member: Member) =
    loungeRepository.exileMember(member)
}