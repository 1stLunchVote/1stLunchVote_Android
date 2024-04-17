package com.jwd.lunchvote.domain.usecase

import com.jwd.lunchvote.domain.repository.LoungeRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetLoungeStatusUseCase @Inject constructor(
    private val repository: LoungeRepository
) {
    operator fun invoke(loungeId: String) = repository.getLoungeStatus(loungeId)
}