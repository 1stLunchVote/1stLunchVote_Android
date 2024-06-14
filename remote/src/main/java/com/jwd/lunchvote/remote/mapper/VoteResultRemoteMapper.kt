package com.jwd.lunchvote.remote.mapper

import com.jwd.lunchvote.core.common.mapper.BiMapper
import com.jwd.lunchvote.data.model.VoteResultData
import com.jwd.lunchvote.remote.model.VoteResultRemote

private object VoteResultRemoteMapper : BiMapper<VoteResultRemote, VoteResultData> {
  override fun mapToRight(from: VoteResultRemote): VoteResultData =
    VoteResultData(
      loungeId = "",
      foodId = from.foodId,
      voteRatio = from.voteRatio
    )

  override fun mapToLeft(from: VoteResultData): VoteResultRemote =
    VoteResultRemote(
      foodId = from.foodId,
      voteRatio = from.voteRatio
    )
}

internal fun VoteResultRemote.asData(loungeId: String): VoteResultData =
  VoteResultRemoteMapper.mapToRight(this).copy(loungeId = loungeId)

internal fun VoteResultData.asRemote(): VoteResultRemote =
  VoteResultRemoteMapper.mapToLeft(this)