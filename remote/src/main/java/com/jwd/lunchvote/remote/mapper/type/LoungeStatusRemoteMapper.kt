package com.jwd.lunchvote.remote.mapper.type

import com.jwd.lunchvote.core.common.error.LoungeError
import com.jwd.lunchvote.core.common.mapper.BiMapper
import com.jwd.lunchvote.data.model.type.LoungeStatusDataType

private object LoungeStatusRemoteMapper : BiMapper<String, LoungeStatusDataType> {
  override fun mapToRight(from: String): LoungeStatusDataType {
    return when (from) {
      "created" -> LoungeStatusDataType.CREATED
      "started" -> LoungeStatusDataType.STARTED
      "finished" -> LoungeStatusDataType.FINISHED
      else -> throw LoungeError.InvalidLoungeStatus
    }
  }

  override fun mapToLeft(from: LoungeStatusDataType): String {
    return when (from) {
      LoungeStatusDataType.CREATED -> "created"
      LoungeStatusDataType.STARTED -> "started"
      LoungeStatusDataType.FINISHED -> "finished"
    }
  }
}

internal fun String.asLoungeStatusDataType(): LoungeStatusDataType {
  return LoungeStatusRemoteMapper.mapToRight(this)
}

internal fun LoungeStatusDataType.asRemote(): String {
  return LoungeStatusRemoteMapper.mapToLeft(this)
}