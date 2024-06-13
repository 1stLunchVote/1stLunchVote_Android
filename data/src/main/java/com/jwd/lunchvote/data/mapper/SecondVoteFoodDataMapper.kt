package com.jwd.lunchvote.data.mapper

import com.jwd.lunchvote.core.common.mapper.BiMapper
import com.jwd.lunchvote.data.model.SecondVoteFoodData
import com.jwd.lunchvote.domain.entity.SecondVoteFood

private object SecondVoteFoodDataMapper : BiMapper<SecondVoteFoodData, SecondVoteFood> {
  override fun mapToRight(from: SecondVoteFoodData): SecondVoteFood =
    SecondVoteFood(
      foodId = from.foodId,
      userIds = from.userIds
    )

  override fun mapToLeft(from: SecondVoteFood): SecondVoteFoodData =
    SecondVoteFoodData(
      foodId = from.foodId,
      userIds = from.userIds
    )
}

internal fun SecondVoteFoodData.asDomain(): SecondVoteFood =
  SecondVoteFoodDataMapper.mapToRight(this)

internal fun SecondVoteFood.asData(): SecondVoteFoodData =
  SecondVoteFoodDataMapper.mapToLeft(this)