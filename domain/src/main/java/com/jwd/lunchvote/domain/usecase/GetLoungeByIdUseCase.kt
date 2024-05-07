package com.jwd.lunchvote.domain.usecase

import com.jwd.lunchvote.domain.repository.LoungeRepository
import javax.inject.Inject

class GetLoungeByIdUseCase @Inject constructor(
    private val loungeRepository: LoungeRepository
) {
  suspend operator fun invoke(id: String) =
    loungeRepository.getLoungeById(id)
}