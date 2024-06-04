package com.jwd.lunchvote.data.mapper

import com.jwd.lunchvote.core.common.mapper.BiMapper
import com.jwd.lunchvote.data.model.LoungeData
import com.jwd.lunchvote.domain.entity.Lounge

private object LoungeDataMapper : BiMapper<LoungeData, Lounge> {
  override fun mapToRight(from: LoungeData): Lounge =
    Lounge(
      id = from.id,
      status = from.status.asDomain(),
      members = from.members
    )

  override fun mapToLeft(from: Lounge): LoungeData =
    LoungeData(
      id = from.id,
      status = from.status.asData(),
      members = from.members
    )
}

private object LoungeStatusDataMapper : BiMapper<LoungeData.Status, Lounge.Status> {
  override fun mapToRight(from: LoungeData.Status): Lounge.Status =
    when (from) {
      LoungeData.Status.CREATED -> Lounge.Status.CREATED
      LoungeData.Status.QUIT -> Lounge.Status.QUIT
      LoungeData.Status.FIRST_VOTE -> Lounge.Status.FIRST_VOTE
      LoungeData.Status.SECOND_VOTE -> Lounge.Status.SECOND_VOTE
      LoungeData.Status.FINISHED -> Lounge.Status.FINISHED
    }

  override fun mapToLeft(from: Lounge.Status): LoungeData.Status =
    when (from) {
      Lounge.Status.CREATED -> LoungeData.Status.CREATED
      Lounge.Status.QUIT -> LoungeData.Status.QUIT
      Lounge.Status.FIRST_VOTE -> LoungeData.Status.FIRST_VOTE
      Lounge.Status.SECOND_VOTE -> LoungeData.Status.SECOND_VOTE
      Lounge.Status.FINISHED -> LoungeData.Status.FINISHED
    }
}

internal fun LoungeData.asDomain(): Lounge =
  LoungeDataMapper.mapToRight(this)

internal fun Lounge.asData(): LoungeData =
  LoungeDataMapper.mapToLeft(this)

internal fun LoungeData.Status.asDomain(): Lounge.Status =
  LoungeStatusDataMapper.mapToRight(this)

internal fun Lounge.Status.asData(): LoungeData.Status =
  LoungeStatusDataMapper.mapToLeft(this)