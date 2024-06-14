package com.jwd.lunchvote.data.mapper

import com.jwd.lunchvote.core.common.mapper.BiMapper
import com.jwd.lunchvote.data.model.FirstVoteResultData
import com.jwd.lunchvote.domain.entity.FirstVoteResult

private object FirstVoteResultDataMapper : BiMapper<FirstVoteResultData, FirstVoteResult> {
  override fun mapToRight(from: FirstVoteResultData): FirstVoteResult =
    FirstVoteResult(
      loungeId = from.loungeId,
      foodIds = from.foodIds
    )

  override fun mapToLeft(from: FirstVoteResult): FirstVoteResultData =
    FirstVoteResultData(
      loungeId = from.loungeId,
      foodIds = from.foodIds
    )
}

internal fun FirstVoteResultData.asDomain(): FirstVoteResult =
  FirstVoteResultDataMapper.mapToRight(this)

internal fun FirstVoteResult.asData(): FirstVoteResultData =
  FirstVoteResultDataMapper.mapToLeft(this)