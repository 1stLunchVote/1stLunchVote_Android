package com.jwd.lunchvote.domain.usecase

import com.jwd.lunchvote.domain.repository.LoungeRepository
import javax.inject.Inject

class JoinLoungeUseCase @Inject constructor(
    private val repository: LoungeRepository
) {
    suspend operator fun invoke(loungeId: String) = repository.joinLounge(loungeId)
}