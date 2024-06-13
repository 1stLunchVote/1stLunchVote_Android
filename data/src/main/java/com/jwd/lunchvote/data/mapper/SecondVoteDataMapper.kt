package com.jwd.lunchvote.data.mapper

import com.jwd.lunchvote.core.common.mapper.BiMapper
import com.jwd.lunchvote.data.model.SecondVoteData
import com.jwd.lunchvote.domain.entity.SecondVote

private object SecondVoteDataMapper: BiMapper<SecondVoteData, SecondVote> {
  override fun mapToRight(from: SecondVoteData): SecondVote =
    SecondVote(
      loungeId = from.loungeId,
      foods = from.foods.map { it.asDomain() }
    )

  override fun mapToLeft(from: SecondVote): SecondVoteData =
    SecondVoteData(
      loungeId = from.loungeId,
      foods = from.foods.map { it.asData() }
    )
}

internal fun SecondVoteData.asDomain(): SecondVote =
  SecondVoteDataMapper.mapToRight(this)

internal fun SecondVote.asData(): SecondVoteData =
  SecondVoteDataMapper.mapToLeft(this)
