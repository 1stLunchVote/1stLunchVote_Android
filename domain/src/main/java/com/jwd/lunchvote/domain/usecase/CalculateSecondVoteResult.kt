package com.jwd.lunchvote.domain.usecase

import com.jwd.lunchvote.domain.repository.LoungeRepository
import com.jwd.lunchvote.domain.repository.SecondVoteRepository
import kotlinx.coroutines.delay
import javax.inject.Inject

class CalculateSecondVoteResult @Inject constructor(
  private val secondVoteRepository: SecondVoteRepository,
  private val loungeRepository: LoungeRepository
) {

  suspend operator fun invoke(loungeId: String): Pair<String, Float> {
    // 모든 사용자가 정보를 저장한 이후에 받을 수 있도록 임의로 대기
    delay(1000)

    val secondVoteResult = secondVoteRepository.getSecondVoteResult(loungeId)

    var maxPoint = 0
    val electedFoodIds = mutableListOf<String>()
    secondVoteResult.foods.forEach {
      if (it.userIds.size > maxPoint) {
        maxPoint = it.userIds.size
        electedFoodIds.clear()
        electedFoodIds.add(it.foodId)
      } else if (it.userIds.size == maxPoint) {
        electedFoodIds.add(it.foodId)
      }
    }

    val members = loungeRepository.getLoungeById(loungeId).members

    return electedFoodIds.random() to maxPoint/members.toFloat()
  }
}