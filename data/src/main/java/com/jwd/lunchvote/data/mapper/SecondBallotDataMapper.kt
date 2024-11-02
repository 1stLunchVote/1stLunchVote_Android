package com.jwd.lunchvote.data.mapper

import com.jwd.lunchvote.mapper.BiMapper
import com.jwd.lunchvote.data.model.SecondBallotData
import com.jwd.lunchvote.domain.entity.SecondBallot

private object SecondBallotDataMapper : BiMapper<SecondBallotData, SecondBallot> {
  override fun mapToRight(from: SecondBallotData): SecondBallot =
    SecondBallot(
      loungeId = from.loungeId,
      userId = from.userId,
      foodId = from.foodId
    )

  override fun mapToLeft(from: SecondBallot): SecondBallotData =
    SecondBallotData(
      loungeId = from.loungeId,
      userId = from.userId,
      foodId = from.foodId
    )
}

internal fun SecondBallotData.asDomain(): SecondBallot =
  SecondBallotDataMapper.mapToRight(this)

internal fun SecondBallot.asData(): SecondBallotData =
  SecondBallotDataMapper.mapToLeft(this)