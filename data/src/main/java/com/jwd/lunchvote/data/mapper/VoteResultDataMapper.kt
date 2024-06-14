package com.jwd.lunchvote.data.mapper

import com.jwd.lunchvote.core.common.mapper.BiMapper
import com.jwd.lunchvote.data.model.VoteResultData
import com.jwd.lunchvote.domain.entity.VoteResult

private object VoteResultDataMapper : BiMapper<VoteResultData, VoteResult> {
  override fun mapToRight(from: VoteResultData): VoteResult =
    VoteResult(
      loungeId = from.loungeId,
      foodId = from.foodId,
      voteRatio = from.voteRatio
    )

  override fun mapToLeft(from: VoteResult): VoteResultData =
    VoteResultData(
      loungeId = from.loungeId,
      foodId = from.foodId,
      voteRatio = from.voteRatio
    )
}

internal fun VoteResultData.asDomain(): VoteResult =
  VoteResultDataMapper.mapToRight(this)

internal fun VoteResult.asData(): VoteResultData =
  VoteResultDataMapper.mapToLeft(this)