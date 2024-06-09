package com.jwd.lunchvote.data.mapper

import com.jwd.lunchvote.core.common.mapper.BiMapper
import com.jwd.lunchvote.data.model.FirstVoteData
import com.jwd.lunchvote.domain.entity.FirstVote

private object FirstVoteDataMapper : BiMapper<FirstVoteData, FirstVote> {
  override fun mapToRight(from: FirstVoteData): FirstVote =
    FirstVote(
      loungeId = from.loungeId,
      userId = from.userId,
      likedFoodIds = from.likedFoodIds,
      dislikedFoodIds = from.dislikedFoodIds
    )

  override fun mapToLeft(from: FirstVote): FirstVoteData =
    FirstVoteData(
      loungeId = from.loungeId,
      userId = from.userId,
      likedFoodIds = from.likedFoodIds,
      dislikedFoodIds = from.dislikedFoodIds
    )
}

internal fun FirstVoteData.asDomain(): FirstVote =
  FirstVoteDataMapper.mapToRight(this)

internal fun FirstVote.asData(): FirstVoteData =
  FirstVoteDataMapper.mapToLeft(this)