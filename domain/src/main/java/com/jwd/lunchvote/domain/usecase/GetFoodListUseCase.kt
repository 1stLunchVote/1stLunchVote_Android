package com.jwd.lunchvote.domain.usecase

import com.jwd.lunchvote.domain.repository.FirstVoteRepository
import javax.inject.Inject

class GetFoodListUseCase @Inject constructor(
  private val firstVoteRepository: FirstVoteRepository
) {
  suspend operator fun invoke() = firstVoteRepository.getFoodList()
}