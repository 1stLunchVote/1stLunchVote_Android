package com.jwd.lunchvote.domain.usecase

import com.jwd.lunchvote.domain.entity.type.LoungeStatusType
import com.jwd.lunchvote.domain.repository.LoungeRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetLoungeStatusUseCase @Inject constructor(
  private val loungeRepository: LoungeRepository
) {
  operator fun invoke(loungeId: String): Flow<LoungeStatusType> =
    loungeRepository.getLoungeStatus(loungeId)
}