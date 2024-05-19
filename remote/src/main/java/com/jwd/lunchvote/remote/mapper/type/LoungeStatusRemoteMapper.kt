package com.jwd.lunchvote.remote.mapper.type

import com.jwd.lunchvote.core.common.error.LoungeError
import com.jwd.lunchvote.core.common.mapper.BiMapper
import com.jwd.lunchvote.data.model.type.LoungeStatusData

private object LoungeStatusRemoteMapper : BiMapper<String, LoungeStatusData> {
  override fun mapToRight(from: String): LoungeStatusData {
    return when (from) {
      "created" -> LoungeStatusData.CREATED
      "quit" -> LoungeStatusData.QUIT
      "started" -> LoungeStatusData.STARTED
      "finished" -> LoungeStatusData.FINISHED
      else -> throw LoungeError.InvalidLoungeStatus
    }
  }

  override fun mapToLeft(from: LoungeStatusData): String {
    return when (from) {
      LoungeStatusData.CREATED -> "created"
      LoungeStatusData.QUIT -> "quit"
      LoungeStatusData.STARTED -> "started"
      LoungeStatusData.FINISHED -> "finished"
    }
  }
}

internal fun String.asLoungeStatusDataType(): LoungeStatusData {
  return LoungeStatusRemoteMapper.mapToRight(this)
}

internal fun LoungeStatusData.asRemote(): String {
  return LoungeStatusRemoteMapper.mapToLeft(this)
}