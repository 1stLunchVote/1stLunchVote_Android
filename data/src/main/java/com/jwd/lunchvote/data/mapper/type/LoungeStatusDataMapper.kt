package com.jwd.lunchvote.data.mapper.type

import com.jwd.lunchvote.core.common.mapper.BiMapper
import com.jwd.lunchvote.data.model.type.LoungeStatusDataType
import com.jwd.lunchvote.domain.entity.type.LoungeStatusType

private object LoungeStatusRemoteMapper : BiMapper<LoungeStatusDataType, LoungeStatusType> {
  override fun mapToRight(from: LoungeStatusDataType): LoungeStatusType {
    return when (from) {
      LoungeStatusDataType.CREATED -> LoungeStatusType.CREATED
      LoungeStatusDataType.STARTED -> LoungeStatusType.STARTED
      LoungeStatusDataType.FINISHED -> LoungeStatusType.FINISHED
    }
  }

  override fun mapToLeft(from: LoungeStatusType): LoungeStatusDataType {
    return when (from) {
      LoungeStatusType.CREATED -> LoungeStatusDataType.CREATED
      LoungeStatusType.STARTED -> LoungeStatusDataType.STARTED
      LoungeStatusType.FINISHED -> LoungeStatusDataType.FINISHED
    }
  }
}

internal fun LoungeStatusDataType.asDomain(): LoungeStatusType {
  return LoungeStatusRemoteMapper.mapToRight(this)
}

internal fun LoungeStatusType.asData(): LoungeStatusDataType {
  return LoungeStatusRemoteMapper.mapToLeft(this)
}