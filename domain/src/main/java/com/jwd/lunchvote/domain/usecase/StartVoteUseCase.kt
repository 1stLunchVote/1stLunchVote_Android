package com.jwd.lunchvote.domain.usecase

import com.jwd.lunchvote.domain.repository.LoungeRepository
import com.jwd.lunchvote.domain.repository.MemberRepository
import javax.inject.Inject

class StartVoteUseCase @Inject constructor(
  private val loungeRepository: LoungeRepository,
  private val memberRepository: MemberRepository
) {

  suspend operator fun invoke(id: String) {
    val memberList = memberRepository.getM(id)
    // TODO: 여기 만들어야함 ㅇㅋ?

    loungeRepository.startLoungeById(id)

  }
}