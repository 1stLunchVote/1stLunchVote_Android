package com.jwd.lunchvote.presentation.mapper

import com.jwd.lunchvote.core.common.mapper.BiMapper
import com.jwd.lunchvote.domain.entity.SecondVoteResult
import com.jwd.lunchvote.presentation.model.SecondVoteResultUIModel

private object VoteResultUIMapper : BiMapper<SecondVoteResultUIModel, SecondVoteResult> {
  override fun mapToRight(from: SecondVoteResultUIModel): SecondVoteResult =
    SecondVoteResult(
      loungeId = from.loungeId,
      foodId = from.foodId,
      voteRatio = from.voteRatio
    )

  override fun mapToLeft(from: SecondVoteResult): SecondVoteResultUIModel =
    SecondVoteResultUIModel(
      loungeId = from.loungeId,
      foodId = from.foodId,
      voteRatio = from.voteRatio
    )
}

internal fun SecondVoteResultUIModel.asDomain(): SecondVoteResult =
  VoteResultUIMapper.mapToRight(this)

internal fun SecondVoteResult.asUI(): SecondVoteResultUIModel =
  VoteResultUIMapper.mapToLeft(this)