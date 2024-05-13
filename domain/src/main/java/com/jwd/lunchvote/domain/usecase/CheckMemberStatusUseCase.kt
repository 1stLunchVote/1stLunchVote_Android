package com.jwd.lunchvote.domain.usecase

import com.jwd.lunchvote.domain.entity.Member
import com.jwd.lunchvote.domain.repository.LoungeRepository
import javax.inject.Inject

class CheckMemberStatusUseCase @Inject constructor(
  private val loungeRepository: LoungeRepository
) {
  operator fun invoke(member: Member) =
      loungeRepository.getMemberStatus(member)
}