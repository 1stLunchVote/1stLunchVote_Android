package com.jwd.lunchvote.domain.usecase.lounge

import com.jwd.lunchvote.domain.repository.LoungeRepository
import javax.inject.Inject

class UpdateReadyUseCase @Inject constructor(
    private val repository: LoungeRepository
) {
    suspend operator fun invoke(uid: String, loungeId: String) = repository.updateReady(uid, loungeId)
}