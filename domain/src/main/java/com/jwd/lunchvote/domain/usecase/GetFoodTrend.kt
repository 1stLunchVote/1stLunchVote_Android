package com.jwd.lunchvote.domain.usecase

import com.jwd.lunchvote.domain.entity.Food
import com.jwd.lunchvote.domain.repository.FoodRepository
import com.jwd.lunchvote.domain.repository.VoteResultRepository
import kr.co.inbody.config.error.VoteResultError
import javax.inject.Inject

class GetFoodTrend @Inject constructor(
  private val voteResultRepository: VoteResultRepository,
  private val foodRepository: FoodRepository
) {

  suspend operator fun invoke(): Pair<Food, Float> {
    val voteResultList = voteResultRepository.getAllVoteResults()
    val foodIdCounts = voteResultList.groupingBy { it.foodId }.eachCount()

    foodIdCounts.maxByOrNull { it.value }?.key?.let { foodId ->
      val food = foodRepository.getFoodById(foodId)
      val ratio = foodIdCounts[foodId]?.toFloat()?.div(voteResultList.size)?.times(100f) ?: 30f

      return food to ratio
    } ?: throw VoteResultError.NoVoteResult
  }
}