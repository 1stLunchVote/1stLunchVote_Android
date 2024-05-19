package com.jwd.lunchvote.data.mapper.type

import com.jwd.lunchvote.core.common.mapper.BiMapper
import com.jwd.lunchvote.data.model.type.LoungeStatusData
import com.jwd.lunchvote.domain.entity.type.LoungeStatus

private object LoungeStatusRemoteMapper : BiMapper<LoungeStatusData, LoungeStatus> {
  override fun mapToRight(from: LoungeStatusData): LoungeStatus {
    return when (from) {
      LoungeStatusData.CREATED -> LoungeStatus.CREATED
      LoungeStatusData.QUIT -> LoungeStatus.QUIT
      LoungeStatusData.STARTED -> LoungeStatus.STARTED
      LoungeStatusData.FINISHED -> LoungeStatus.FINISHED
    }
  }

  override fun mapToLeft(from: LoungeStatus): LoungeStatusData {
    return when (from) {
      LoungeStatus.CREATED -> LoungeStatusData.CREATED
      LoungeStatus.QUIT -> LoungeStatusData.QUIT
      LoungeStatus.STARTED -> LoungeStatusData.STARTED
      LoungeStatus.FINISHED -> LoungeStatusData.FINISHED
    }
  }
}

internal fun LoungeStatusData.asDomain(): LoungeStatus {
  return LoungeStatusRemoteMapper.mapToRight(this)
}

internal fun LoungeStatus.asData(): LoungeStatusData {
  return LoungeStatusRemoteMapper.mapToLeft(this)
}