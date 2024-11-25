package com.jwd.lunchvote.presentation.mapper

import com.jwd.lunchvote.domain.entity.SecondBallot
import com.jwd.lunchvote.mapper.BiMapper
import com.jwd.lunchvote.presentation.model.SecondBallotUIModel

private object SecondBallotUIMapper : BiMapper<SecondBallotUIModel, SecondBallot> {
  override fun mapToRight(from: SecondBallotUIModel): SecondBallot =
    SecondBallot(
      loungeId = from.loungeId,
      userId = from.userId,
      foodId = from.foodId
    )

  override fun mapToLeft(from: SecondBallot): SecondBallotUIModel =
    SecondBallotUIModel(
      loungeId = from.loungeId,
      userId = from.userId,
      foodId = from.foodId
    )
}

internal fun SecondBallotUIModel.asDomain(): SecondBallot =
  SecondBallotUIMapper.mapToRight(this)

internal fun SecondBallot.asUI(): SecondBallotUIModel =
  SecondBallotUIMapper.mapToLeft(this)