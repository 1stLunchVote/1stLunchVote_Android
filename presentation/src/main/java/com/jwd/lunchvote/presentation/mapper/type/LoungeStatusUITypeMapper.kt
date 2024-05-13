package com.jwd.lunchvote.presentation.mapper.type

import com.jwd.lunchvote.core.common.mapper.BiMapper
import com.jwd.lunchvote.domain.entity.type.LoungeStatusType
import com.jwd.lunchvote.presentation.model.type.LoungeStatusUIType

private object LoungeStatusUITypeMapper : BiMapper<LoungeStatusUIType, LoungeStatusType> {
  override fun mapToRight(from: LoungeStatusUIType): LoungeStatusType {
    return when (from) {
      LoungeStatusUIType.CREATED -> LoungeStatusType.CREATED
      LoungeStatusUIType.STARTED -> LoungeStatusType.STARTED
      LoungeStatusUIType.FINISHED -> LoungeStatusType.FINISHED
    }
  }

  override fun mapToLeft(from: LoungeStatusType): LoungeStatusUIType {
    return when (from) {
      LoungeStatusType.CREATED -> LoungeStatusUIType.CREATED
      LoungeStatusType.STARTED -> LoungeStatusUIType.STARTED
      LoungeStatusType.FINISHED -> LoungeStatusUIType.FINISHED
    }
  }
}

internal fun LoungeStatusUIType.asDomain(): LoungeStatusType {
  return LoungeStatusUITypeMapper.mapToRight(this)
}

internal fun LoungeStatusType.asUI(): LoungeStatusUIType {
  return LoungeStatusUITypeMapper.mapToLeft(this)
}