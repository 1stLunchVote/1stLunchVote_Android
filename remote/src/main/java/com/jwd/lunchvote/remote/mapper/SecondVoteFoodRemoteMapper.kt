package com.jwd.lunchvote.remote.mapper

import com.jwd.lunchvote.core.common.mapper.BiMapper
import com.jwd.lunchvote.data.model.SecondVoteFoodData
import com.jwd.lunchvote.remote.model.SecondVoteFoodRemote

private object SecondVoteFoodRemoteMapper : BiMapper<SecondVoteFoodRemote, SecondVoteFoodData> {
  override fun mapToRight(from: SecondVoteFoodRemote): SecondVoteFoodData =
    SecondVoteFoodData(
      foodId = from.foodId,
      userIds = from.userIds
    )

  override fun mapToLeft(from: SecondVoteFoodData): SecondVoteFoodRemote =
    SecondVoteFoodRemote(
      foodId = from.foodId,
      userIds = from.userIds
    )
}

internal fun SecondVoteFoodRemote.asData(): SecondVoteFoodData =
  SecondVoteFoodRemoteMapper.mapToRight(this)

internal fun SecondVoteFoodData.asRemote(): SecondVoteFoodRemote =
  SecondVoteFoodRemoteMapper.mapToLeft(this)