package com.jwd.lunchvote.presentation.mapper.type

import com.jwd.lunchvote.core.common.mapper.BiMapper
import com.jwd.lunchvote.domain.entity.type.LoungeStatus
import com.jwd.lunchvote.presentation.model.type.LoungeStatusUIType

private object LoungeStatusUITypeMapper : BiMapper<LoungeStatusUIType, LoungeStatus> {
  override fun mapToRight(from: LoungeStatusUIType): LoungeStatus {
    return when (from) {
      LoungeStatusUIType.CREATED -> LoungeStatus.CREATED
      LoungeStatusUIType.QUIT -> LoungeStatus.QUIT
      LoungeStatusUIType.STARTED -> LoungeStatus.STARTED
      LoungeStatusUIType.FINISHED -> LoungeStatus.FINISHED
    }
  }

  override fun mapToLeft(from: LoungeStatus): LoungeStatusUIType {
    return when (from) {
      LoungeStatus.CREATED -> LoungeStatusUIType.CREATED
      LoungeStatus.QUIT -> LoungeStatusUIType.QUIT
      LoungeStatus.STARTED -> LoungeStatusUIType.STARTED
      LoungeStatus.FINISHED -> LoungeStatusUIType.FINISHED
    }
  }
}

internal fun LoungeStatusUIType.asDomain(): LoungeStatus {
  return LoungeStatusUITypeMapper.mapToRight(this)
}

internal fun LoungeStatus.asUI(): LoungeStatusUIType {
  return LoungeStatusUITypeMapper.mapToLeft(this)
}