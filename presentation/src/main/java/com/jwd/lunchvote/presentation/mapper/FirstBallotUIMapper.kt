package com.jwd.lunchvote.presentation.mapper

import com.jwd.lunchvote.mapper.BiMapper
import com.jwd.lunchvote.domain.entity.FirstBallot
import com.jwd.lunchvote.presentation.model.FirstBallotUIModel

private object FirstBallotUIMapper : BiMapper<FirstBallotUIModel, FirstBallot> {
  override fun mapToRight(from: FirstBallotUIModel): FirstBallot =
    FirstBallot(
      loungeId = from.loungeId,
      userId = from.userId,
      likedFoodIds = from.likedFoodIds,
      dislikedFoodIds = from.dislikedFoodIds
    )

  override fun mapToLeft(from: FirstBallot): FirstBallotUIModel =
    FirstBallotUIModel(
      loungeId = from.loungeId,
      userId = from.userId,
      likedFoodIds = from.likedFoodIds,
      dislikedFoodIds = from.dislikedFoodIds
    )
}

internal fun FirstBallotUIModel.asDomain(): FirstBallot =
  FirstBallotUIMapper.mapToRight(this)

internal fun FirstBallot.asUI(): FirstBallotUIModel =
  FirstBallotUIMapper.mapToLeft(this)