package com.jwd.lunchvote.remote.mapper

import kr.co.inbody.library.mapper.BiMapper
import com.jwd.lunchvote.data.model.SecondVoteResultData
import com.jwd.lunchvote.remote.model.SecondVoteResultRemote

private object VoteResultRemoteMapper : BiMapper<SecondVoteResultRemote, SecondVoteResultData> {
  override fun mapToRight(from: SecondVoteResultRemote): SecondVoteResultData =
    SecondVoteResultData(
      loungeId = "",
      foodId = from.foodId,
      voteRatio = from.voteRatio
    )

  override fun mapToLeft(from: SecondVoteResultData): SecondVoteResultRemote =
    SecondVoteResultRemote(
      foodId = from.foodId,
      voteRatio = from.voteRatio
    )
}

internal fun SecondVoteResultRemote.asData(loungeId: String): SecondVoteResultData =
  VoteResultRemoteMapper.mapToRight(this).copy(loungeId = loungeId)

internal fun SecondVoteResultData.asRemote(): SecondVoteResultRemote =
  VoteResultRemoteMapper.mapToLeft(this)