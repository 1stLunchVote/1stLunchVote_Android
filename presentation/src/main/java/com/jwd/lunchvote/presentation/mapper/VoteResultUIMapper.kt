package com.jwd.lunchvote.presentation.mapper

import com.jwd.lunchvote.core.common.mapper.BiMapper
import com.jwd.lunchvote.domain.entity.VoteResult
import com.jwd.lunchvote.presentation.model.VoteResultUIModel

private object VoteResultUIMapper : BiMapper<VoteResultUIModel, VoteResult> {
  override fun mapToRight(from: VoteResultUIModel): VoteResult =
    VoteResult(
      loungeId = from.loungeId,
      foodId = from.foodId,
      voteRatio = from.voteRatio
    )

  override fun mapToLeft(from: VoteResult): VoteResultUIModel =
    VoteResultUIModel(
      loungeId = from.loungeId,
      foodId = from.foodId,
      voteRatio = from.voteRatio
    )
}

internal fun VoteResultUIModel.asDomain(): VoteResult =
  VoteResultUIMapper.mapToRight(this)

internal fun VoteResult.asUI(): VoteResultUIModel =
  VoteResultUIMapper.mapToLeft(this)