package com.jwd.lunchvote.remote.mapper

import com.jwd.lunchvote.mapper.BiMapper
import com.jwd.lunchvote.data.model.FirstVoteResultData
import com.jwd.lunchvote.remote.model.FirstVoteResultRemote

private object FirstVoteResultRemoteMapper : BiMapper<FirstVoteResultRemote, FirstVoteResultData> {
  override fun mapToRight(from: FirstVoteResultRemote): FirstVoteResultData =
    FirstVoteResultData(
      loungeId = "",
      foodIds = from.foodIds
    )

  override fun mapToLeft(from: FirstVoteResultData): FirstVoteResultRemote =
    FirstVoteResultRemote(
      foodIds = from.foodIds
    )
}

internal fun FirstVoteResultRemote.asData(loungeId: String): FirstVoteResultData =
  FirstVoteResultRemoteMapper.mapToRight(this).copy(loungeId = loungeId)

internal fun FirstVoteResultData.asRemote(): FirstVoteResultRemote =
  FirstVoteResultRemoteMapper.mapToLeft(this)