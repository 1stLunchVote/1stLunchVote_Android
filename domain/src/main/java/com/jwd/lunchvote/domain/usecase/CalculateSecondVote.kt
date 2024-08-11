package com.jwd.lunchvote.domain.usecase

import com.jwd.lunchvote.domain.entity.SecondVoteResult
import com.jwd.lunchvote.domain.repository.BallotRepository
import com.jwd.lunchvote.domain.repository.VoteResultRepository
import kotlinx.coroutines.delay
import javax.inject.Inject

class CalculateSecondVote @Inject constructor(
  private val ballotRepository: BallotRepository,
  private val voteResultRepository: VoteResultRepository
) {

  suspend operator fun invoke(loungeId: String) {
    // 모든 사용자가 정보를 저장한 이후에 받을 수 있도록 임의로 대기
    delay(1000)

    val secondBallotList = ballotRepository.getAllSecondBallotByLoungeId(loungeId)

    val foodScore = mutableMapOf<String, Int>()
    for (ballot in secondBallotList) {
      foodScore[ballot.foodId] = foodScore.getOrDefault(ballot.foodId, 0) + 1
    }

    val maxScore = foodScore.values.max()
    val electedFoodIds = foodScore.filter { it.value == maxScore }.keys.toList()

    val voteResult = SecondVoteResult(
      loungeId = loungeId,
      foodId = electedFoodIds.random(),
      voteRatio = maxScore.toFloat()/secondBallotList.size.toFloat()
    )
    voteResultRepository.saveSecondVoteResult(voteResult)
  }
}