package com.jwd.lunchvote.presentation.mapper

import com.jwd.lunchvote.core.common.mapper.BiMapper
import com.jwd.lunchvote.domain.entity.SecondVoteFood
import com.jwd.lunchvote.presentation.model.SecondVoteFoodUIModel

private object SecondVoteFoodUIMapper : BiMapper<SecondVoteFoodUIModel, SecondVoteFood> {
  override fun mapToRight(from: SecondVoteFoodUIModel): SecondVoteFood =
    SecondVoteFood(
      foodId = from.foodId,
      userIds = from.userIds
    )

  override fun mapToLeft(from: SecondVoteFood): SecondVoteFoodUIModel =
    SecondVoteFoodUIModel(
      foodId = from.foodId,
      userIds = from.userIds
    )
}

internal fun SecondVoteFoodUIModel.asDomain(): SecondVoteFood =
  SecondVoteFoodUIMapper.mapToRight(this)

internal fun SecondVoteFood.asUI(): SecondVoteFoodUIModel =
  SecondVoteFoodUIMapper.mapToLeft(this)