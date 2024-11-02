package com.jwd.lunchvote.remote.mapper

import kr.co.inbody.library.mapper.BiMapper
import com.jwd.lunchvote.data.model.SecondBallotData
import com.jwd.lunchvote.remote.model.SecondBallotRemote

private object SecondBallotRemoteMapper : BiMapper<SecondBallotRemote, SecondBallotData> {
  override fun mapToRight(from: SecondBallotRemote): SecondBallotData =
    SecondBallotData(
      loungeId = "",
      userId = "",
      foodId = from.foodId
    )

  override fun mapToLeft(from: SecondBallotData): SecondBallotRemote =
    SecondBallotRemote(
      foodId = from.foodId
    )
}

internal fun SecondBallotRemote.asData(loungeId: String, userId: String): SecondBallotData =
  SecondBallotRemoteMapper.mapToRight(this).copy(loungeId = loungeId, userId = userId)

internal fun SecondBallotData.asRemote(): SecondBallotRemote =
  SecondBallotRemoteMapper.mapToLeft(this)