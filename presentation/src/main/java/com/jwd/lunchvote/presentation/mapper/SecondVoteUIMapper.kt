package com.jwd.lunchvote.presentation.mapper

import com.jwd.lunchvote.core.common.mapper.BiMapper
import com.jwd.lunchvote.domain.entity.SecondVote
import com.jwd.lunchvote.presentation.model.SecondVoteUIModel

private object SecondVoteUIMapper : BiMapper<SecondVoteUIModel, SecondVote> {
  override fun mapToRight(from: SecondVoteUIModel): SecondVote =
    SecondVote(
      loungeId = from.loungeId,
      foods = from.foods.map { it.asDomain() }
    )

  override fun mapToLeft(from: SecondVote): SecondVoteUIModel =
    SecondVoteUIModel(
      loungeId = from.loungeId,
      foods = from.foods.map { it.asUI() }
    )
}

internal fun SecondVoteUIModel.asDomain(): SecondVote =
  SecondVoteUIMapper.mapToRight(this)

internal fun SecondVote.asUI(): SecondVoteUIModel =
  SecondVoteUIMapper.mapToLeft(this)