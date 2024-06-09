package com.jwd.lunchvote.presentation.mapper

import com.jwd.lunchvote.core.common.mapper.BiMapper
import com.jwd.lunchvote.domain.entity.FirstVote
import com.jwd.lunchvote.presentation.model.FirstVoteUIModel

private object FirstVoteUIMapper : BiMapper<FirstVoteUIModel, FirstVote> {
  override fun mapToRight(from: FirstVoteUIModel): FirstVote =
    FirstVote(
      loungeId = from.loungeId,
      userId = from.userId,
      likedFoodIds = from.likedFoodIds,
      dislikedFoodIds = from.dislikedFoodIds
    )

  override fun mapToLeft(from: FirstVote): FirstVoteUIModel =
    FirstVoteUIModel(
      loungeId = from.loungeId,
      userId = from.userId,
      likedFoodIds = from.likedFoodIds,
      dislikedFoodIds = from.dislikedFoodIds
    )
}

internal fun FirstVoteUIModel.asDomain(): FirstVote =
  FirstVoteUIMapper.mapToRight(this)

internal fun FirstVote.asUI(): FirstVoteUIModel =
  FirstVoteUIMapper.mapToLeft(this)