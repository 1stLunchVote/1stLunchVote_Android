package com.jwd.lunchvote.presentation.mapper

import com.jwd.lunchvote.mapper.BiMapper
import com.jwd.lunchvote.domain.entity.FirstVoteResult
import com.jwd.lunchvote.presentation.model.FirstVoteResultUIModel

private object FirstVoteResultUIMapper : BiMapper<FirstVoteResultUIModel, FirstVoteResult> {
  override fun mapToRight(from: FirstVoteResultUIModel): FirstVoteResult =
    FirstVoteResult(
      loungeId = from.loungeId,
      foodIds = from.foodIds
    )

  override fun mapToLeft(from: FirstVoteResult): FirstVoteResultUIModel =
    FirstVoteResultUIModel(
      loungeId = from.loungeId,
      foodIds = from.foodIds
    )
}

internal fun FirstVoteResultUIModel.asDomain(): FirstVoteResult =
  FirstVoteResultUIMapper.mapToRight(this)

internal fun FirstVoteResult.asUI(): FirstVoteResultUIModel =
  FirstVoteResultUIMapper.mapToLeft(this)