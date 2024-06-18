package com.jwd.lunchvote.domain.usecase

import com.jwd.lunchvote.domain.entity.FirstVoteResult
import com.jwd.lunchvote.domain.repository.BallotRepository
import com.jwd.lunchvote.domain.repository.FoodRepository
import com.jwd.lunchvote.domain.repository.VoteResultRepository
import kotlinx.coroutines.delay
import kr.co.inbody.config.config.VoteConfig
import javax.inject.Inject

class CalculateFirstVote @Inject constructor(
  private val ballotRepository: BallotRepository,
  private val foodRepository: FoodRepository,
  private val voteResultRepository: VoteResultRepository
) {

  suspend operator fun invoke(loungeId: String) {
    // 모든 사용자가 정보를 저장한 이후에 받을 수 있도록 임의로 대기
    delay(1000)

    val firstBallotList = ballotRepository.getAllFirstBallotByLoungeId(loungeId)

    val foodList = foodRepository.getAllFood().shuffled()
    val foodScore = foodList.associate { it.id to 0 }.toMutableMap()
    for (ballot in firstBallotList) {
      for (likedFoodId in ballot.likedFoodIds) {
        foodScore[likedFoodId] = foodScore.getOrDefault(likedFoodId, 0) + 1
      }
      for (dislikedFoodId in ballot.dislikedFoodIds) {
        foodScore[dislikedFoodId] = foodScore.getOrDefault(dislikedFoodId, 0) - 5
      }
    }

    val limit = VoteConfig.SECOND_VOTE_FOOD_COUNT
    val topFoods = foodScore.entries.sortedByDescending { it.value }.take(limit)

    val minScore = topFoods.last().value
    val topFoodsWithoutMinScoreFoods = topFoods.filter { it.value > minScore }
    val minScoreFoods = foodScore.entries.filter { it.value == minScore }.shuffled()
    val shuffledTopRankingFoods = (topFoodsWithoutMinScoreFoods + minScoreFoods).take(limit).shuffled()

    val voteResult = FirstVoteResult(
      loungeId = loungeId,
      foodIds = shuffledTopRankingFoods.map { it.key }
    )
    voteResultRepository.saveFirstVoteResult(voteResult)
  }
}