package com.jwd.lunchvote.data.mapper

import com.jwd.lunchvote.core.common.mapper.BiMapper
import com.jwd.lunchvote.data.model.FirstBallotData
import com.jwd.lunchvote.domain.entity.FirstBallot

private object FirstVoteDataMapper : BiMapper<FirstBallotData, FirstBallot> {
  override fun mapToRight(from: FirstBallotData): FirstBallot =
    FirstBallot(
      loungeId = from.loungeId,
      userId = from.userId,
      likedFoodIds = from.likedFoodIds,
      dislikedFoodIds = from.dislikedFoodIds
    )

  override fun mapToLeft(from: FirstBallot): FirstBallotData =
    FirstBallotData(
      loungeId = from.loungeId,
      userId = from.userId,
      likedFoodIds = from.likedFoodIds,
      dislikedFoodIds = from.dislikedFoodIds
    )
}

internal fun FirstBallotData.asDomain(): FirstBallot =
  FirstVoteDataMapper.mapToRight(this)

internal fun FirstBallot.asData(): FirstBallotData =
  FirstVoteDataMapper.mapToLeft(this)