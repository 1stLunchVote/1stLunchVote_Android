package com.jwd.lunchvote.presentation.mapper

import com.jwd.lunchvote.core.common.mapper.BiMapper
import com.jwd.lunchvote.domain.entity.Lounge
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

private object LoungeStatusUIMapper : BiMapper<LoungeUIModel.Status, Lounge.Status> {
  override fun mapToRight(from: LoungeUIModel.Status): Lounge.Status {
    return when (from) {
      LoungeUIModel.Status.CREATED -> Lounge.Status.CREATED
      LoungeUIModel.Status.QUIT -> Lounge.Status.QUIT
      LoungeUIModel.Status.STARTED -> Lounge.Status.STARTED
      LoungeUIModel.Status.FINISHED -> Lounge.Status.FINISHED
    }
  }

  override fun mapToLeft(from: Lounge.Status): LoungeUIModel.Status {
    return when (from) {
      Lounge.Status.CREATED -> LoungeUIModel.Status.CREATED
      Lounge.Status.QUIT -> LoungeUIModel.Status.QUIT
      Lounge.Status.STARTED -> LoungeUIModel.Status.STARTED
      Lounge.Status.FINISHED -> LoungeUIModel.Status.FINISHED
    }
  }
}

internal fun LoungeUIModel.Status.asDomain(): Lounge.Status {
  return LoungeStatusUIMapper.mapToRight(this)
}

internal fun Lounge.Status.asUI(): LoungeUIModel.Status {
  return LoungeStatusUIMapper.mapToLeft(this)
}