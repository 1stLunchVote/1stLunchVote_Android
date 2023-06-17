package com.jwd.lunchvote.domain.usecase.lounge

import com.jwd.lunchvote.domain.repository.LoungeRepository
import javax.inject.Inject

class GetMemberListUseCase @Inject constructor(
    private val repository: LoungeRepository
) {
    operator fun invoke(loungeId: String) = repository.getMemberList(loungeId)
}