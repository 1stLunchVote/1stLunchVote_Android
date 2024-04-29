package com.jwd.lunchvote.domain.usecase

import com.jwd.lunchvote.domain.repository.LoungeRepository
import javax.inject.Inject

class CheckMemberStatusUseCase @Inject constructor(
    private val repository: LoungeRepository
) {
    operator fun invoke(uid: String, loungeId: String) = repository.getMemberStatus(uid, loungeId)

}