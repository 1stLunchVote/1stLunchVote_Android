package com.jwd.lunchvote.data.mapper

import com.jwd.lunchvote.core.common.mapper.BiMapper
import com.jwd.lunchvote.data.mapper.type.asData
import com.jwd.lunchvote.data.mapper.type.asDomain
import com.jwd.lunchvote.data.model.LoungeData
import com.jwd.lunchvote.domain.entity.Lounge

private object LoungeDataMapper : BiMapper<LoungeData, Lounge> {
  override fun mapToRight(from: LoungeData): Lounge {
    return Lounge(
      id = from.id,
      status = from.status.asDomain(),
      members = from.members
    )
  }

  override fun mapToLeft(from: Lounge): LoungeData {
    return LoungeData(
      id = from.id,
      status = from.status.asData(),
      members = from.members
    )
  }
}

internal fun LoungeData.asDomain(): Lounge {
  return LoungeDataMapper.mapToRight(this)
}

internal fun Lounge.asData(): LoungeData {
  return LoungeDataMapper.mapToLeft(this)
}