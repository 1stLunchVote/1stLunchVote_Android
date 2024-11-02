package com.jwd.lunchvote.remote.mapper

import com.jwd.lunchvote.mapper.BiMapper
import com.jwd.lunchvote.data.model.FirstBallotData
import com.jwd.lunchvote.remote.model.FirstBallotRemote

private object FirstVoteRemoteMapper : BiMapper<FirstBallotRemote, FirstBallotData> {
  override fun mapToRight(from: FirstBallotRemote): FirstBallotData =
    FirstBallotData(
      loungeId = "",
      userId = "",
      likedFoodIds = from.likedFoodIds,
      dislikedFoodIds = from.dislikedFoodIds
    )

  override fun mapToLeft(from: FirstBallotData): FirstBallotRemote =
    FirstBallotRemote(
      likedFoodIds = from.likedFoodIds,
      dislikedFoodIds = from.dislikedFoodIds
    )
}

internal fun FirstBallotRemote.asData(loungeId: String, userId: String): FirstBallotData =
  FirstVoteRemoteMapper.mapToRight(this).copy(loungeId = loungeId, userId = userId)

internal fun FirstBallotData.asRemote(): FirstBallotRemote =
  FirstVoteRemoteMapper.mapToLeft(this)