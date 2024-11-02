package com.jwd.lunchvote.data.mapper

import com.jwd.lunchvote.mapper.BiMapper
import com.jwd.lunchvote.data.model.SecondVoteResultData
import com.jwd.lunchvote.domain.entity.SecondVoteResult

private object VoteResultDataMapper : BiMapper<SecondVoteResultData, SecondVoteResult> {
  override fun mapToRight(from: SecondVoteResultData): SecondVoteResult =
    SecondVoteResult(
      loungeId = from.loungeId,
      foodId = from.foodId,
      voteRatio = from.voteRatio
    )

  override fun mapToLeft(from: SecondVoteResult): SecondVoteResultData =
    SecondVoteResultData(
      loungeId = from.loungeId,
      foodId = from.foodId,
      voteRatio = from.voteRatio
    )
}

internal fun SecondVoteResultData.asDomain(): SecondVoteResult =
  VoteResultDataMapper.mapToRight(this)

internal fun SecondVoteResult.asData(): SecondVoteResultData =
  VoteResultDataMapper.mapToLeft(this)