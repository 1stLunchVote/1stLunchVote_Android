package com.jwd.lunchvote.domain.usecase

import com.jwd.lunchvote.domain.entity.Food
import com.jwd.lunchvote.domain.repository.HomeRepository
import javax.inject.Inject

class GetFoodTrendUseCase @Inject constructor(
  private val homeRepository: HomeRepository
) {
  suspend operator fun invoke(): Pair<Food, Float> = homeRepository.getFoodTrend()
}