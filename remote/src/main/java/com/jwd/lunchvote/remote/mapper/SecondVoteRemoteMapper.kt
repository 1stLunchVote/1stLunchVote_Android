package com.jwd.lunchvote.remote.mapper

import com.jwd.lunchvote.core.common.mapper.BiMapper
import com.jwd.lunchvote.data.model.SecondVoteData
import com.jwd.lunchvote.remote.model.SecondVoteRemote

private object SecondVoteRemoteMapper: BiMapper<SecondVoteRemote, SecondVoteData> {
  override fun mapToRight(from: SecondVoteRemote): SecondVoteData =
    SecondVoteData(
      loungeId = from.loungeId,
      foods = from.foods.map { it.asData() }
    )

  override fun mapToLeft(from: SecondVoteData): SecondVoteRemote =
    SecondVoteRemote(
      loungeId = from.loungeId,
      foods = from.foods.map { it.asRemote() }
    )
}

internal fun SecondVoteRemote.asData(): SecondVoteData =
  SecondVoteRemoteMapper.mapToRight(this)

internal fun SecondVoteData.asRemote(): SecondVoteRemote =
  SecondVoteRemoteMapper.mapToLeft(this)