package com.jwd.lunchvote.domain.usecase

import com.jwd.lunchvote.domain.repository.FirstVoteRepository
import com.jwd.lunchvote.domain.repository.FoodRepository
import kotlinx.coroutines.delay
import javax.inject.Inject

class CalculateFirstVoteResult @Inject constructor(
  private val firstVoteRepository: FirstVoteRepository,
  private val foodRepository: FoodRepository
) {

  suspend operator fun invoke(loungeId: String): List<String> {
    // 모든 사용자가 정보를 저장한 이후에 받을 수 있도록 임의로 대기
    delay(1000)

    val firstVotes = firstVoteRepository.getAllFirstVotes(loungeId)

    val foodList = foodRepository.getAllFood().shuffled()
    val scoreMap = foodList.associate { it.id to 0 }.toMutableMap()
    for (vote in firstVotes) {
      for (likedFoodId in vote.likedFoodIds) {
        scoreMap[likedFoodId] = scoreMap.getOrDefault(likedFoodId, 0) + 1
      }
      for (dislikedFoodId in vote.dislikedFoodIds) {
        scoreMap[dislikedFoodId] = scoreMap.getOrDefault(dislikedFoodId, 0) - 5
      }
    }

    val limit = 5
    return scoreMap.entries.sortedByDescending { it.value }.map { it.key }.take(limit)
  }
}