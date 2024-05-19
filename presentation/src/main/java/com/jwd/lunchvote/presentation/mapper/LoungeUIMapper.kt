package com.jwd.lunchvote.presentation.mapper

import com.jwd.lunchvote.core.common.mapper.BiMapper
import com.jwd.lunchvote.domain.entity.Lounge
import com.jwd.lunchvote.presentation.mapper.type.asDomain
import com.jwd.lunchvote.presentation.mapper.type.asUI
import com.jwd.lunchvote.presentation.model.LoungeUIModel

private object LoungeUIMapper : BiMapper<LoungeUIModel, Lounge> {
  override fun mapToRight(from: LoungeUIModel): Lounge {
    return Lounge(
      id = from.id,
      status = from.status.asDomain(),
      members = from.members
    )
  }

  override fun mapToLeft(from: Lounge): LoungeUIModel {
    return LoungeUIModel(
      id = from.id,
      status = from.status.asUI(),
      members = from.members
    )
  }
}

internal fun LoungeUIModel.asData(): Lounge {
  return LoungeUIMapper.mapToRight(this)
}

internal fun Lounge.asUI(): LoungeUIModel {
  return LoungeUIMapper.mapToLeft(this)
}