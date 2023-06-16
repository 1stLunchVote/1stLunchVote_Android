package com.jwd.lunchvote.domain.usecase.lounge

import com.jwd.lunchvote.domain.repository.LoungeRepository
import javax.inject.Inject

class ExitLoungeUseCase @Inject constructor(
    private val loungeRepository: LoungeRepository
) {
    operator fun invoke(uid: String, loungeId: String) = loungeRepository.exitLounge(uid, loungeId)
}