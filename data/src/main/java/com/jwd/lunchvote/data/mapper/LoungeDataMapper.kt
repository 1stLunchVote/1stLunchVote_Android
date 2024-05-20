package com.jwd.lunchvote.data.mapper

import com.jwd.lunchvote.core.common.mapper.BiMapper
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

private object LoungeStatusDataMapper : BiMapper<LoungeData.Status, Lounge.Status> {
  override fun mapToRight(from: LoungeData.Status): Lounge.Status {
    return when (from) {
      LoungeData.Status.CREATED -> Lounge.Status.CREATED
      LoungeData.Status.QUIT -> Lounge.Status.QUIT
      LoungeData.Status.STARTED -> Lounge.Status.STARTED
      LoungeData.Status.FINISHED -> Lounge.Status.FINISHED
    }
  }

  override fun mapToLeft(from: Lounge.Status): LoungeData.Status {
    return when (from) {
      Lounge.Status.CREATED -> LoungeData.Status.CREATED
      Lounge.Status.QUIT -> LoungeData.Status.QUIT
      Lounge.Status.STARTED -> LoungeData.Status.STARTED
      Lounge.Status.FINISHED -> LoungeData.Status.FINISHED
    }
  }
}

internal fun LoungeData.Status.asDomain(): Lounge.Status {
  return LoungeStatusDataMapper.mapToRight(this)
}

internal fun Lounge.Status.asData(): LoungeData.Status {
  return LoungeStatusDataMapper.mapToLeft(this)
}