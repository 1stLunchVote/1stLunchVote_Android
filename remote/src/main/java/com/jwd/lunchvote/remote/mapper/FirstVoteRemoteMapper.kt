package com.jwd.lunchvote.remote.mapper

import com.jwd.lunchvote.core.common.mapper.BiMapper
import com.jwd.lunchvote.data.model.FirstVoteData
import com.jwd.lunchvote.remote.model.FirstVoteRemote

private object FirstVoteRemoteMapper : BiMapper<FirstVoteRemote, FirstVoteData> {
  override fun mapToRight(from: FirstVoteRemote): FirstVoteData =
    FirstVoteData(
      loungeId = "",
      userId = "",
      likedFoodIds = from.likedFoodIds,
      dislikedFoodIds = from.dislikedFoodIds
    )

  override fun mapToLeft(from: FirstVoteData): FirstVoteRemote =
    FirstVoteRemote(
      likedFoodIds = from.likedFoodIds,
      dislikedFoodIds = from.dislikedFoodIds
    )
}

internal fun FirstVoteRemote.asData(loungeId: String, userId: String): FirstVoteData =
  FirstVoteRemoteMapper.mapToRight(this).copy(loungeId = loungeId, userId = userId)

internal fun FirstVoteData.asRemote(): FirstVoteRemote =
  FirstVoteRemoteMapper.mapToLeft(this)