package com.jwd.lunchvote.domain.usecase.lounge

import com.jwd.lunchvote.domain.repository.LoungeRepository
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CheckMemberStatusUseCase @Inject constructor(
    private val repository: LoungeRepository
) {
    operator fun invoke(uid: String, loungeId: String) = repository.getMemberStatus(uid, loungeId)

}