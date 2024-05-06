package com.jwd.lunchvote.presentation.mapper

import com.jwd.lunchvote.core.common.mapper.BiMapper
import com.jwd.lunchvote.domain.entity.Lounge
import com.jwd.lunchvote.presentation.mapper.type.asDomain
import com.jwd.lunchvote.presentation.mapper.type.asUI
import com.jwd.lunchvote.presentation.model.LoungeUIModel

private object LoungeUIMapper : BiMapper<LoungeUIModel, Lounge> {
  override fun mapToRight(from: LoungeUIModel): Lounge {
    return Lounge(
      id = "",
      status = from.status.asDomain(),
      members = from.members.map { it.asDomain() }
    )
  }

  override fun mapToLeft(from: Lounge): LoungeUIModel {
    return LoungeUIModel(
      status = from.status.asUI(),
      members = from.members.map { it.asUI() }
    )
  }
}

internal fun LoungeUIModel.asData(id: String): Lounge {
  return LoungeUIMapper.mapToRight(this).copy(id = id)
}

internal fun Lounge.asUI(): LoungeUIModel {
  return LoungeUIMapper.mapToLeft(this)
}