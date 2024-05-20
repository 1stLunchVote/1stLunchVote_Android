package com.jwd.lunchvote.remote.mapper

import com.jwd.lunchvote.core.common.error.LoungeError
import com.jwd.lunchvote.core.common.mapper.BiMapper
import com.jwd.lunchvote.data.model.LoungeData
import com.jwd.lunchvote.remote.model.LoungeRemote

private object LoungeRemoteMapper: BiMapper<LoungeRemote, LoungeData> {
  override fun mapToRight(from: LoungeRemote): LoungeData {
    return LoungeData(
      id = "",
      status = from.status.asLoungeStatusData(),
      members = from.members
    )
  }

  override fun mapToLeft(from: LoungeData): LoungeRemote {
    return LoungeRemote(
      status = from.status.asRemote(),
      members = from.members
    )
  }
}

internal fun LoungeRemote.asData(id: String): LoungeData {
  return LoungeRemoteMapper.mapToRight(this).copy(id = id)
}

internal fun LoungeData.asRemote(): LoungeRemote {
  return LoungeRemoteMapper.mapToLeft(this)
}

private object LoungeStatusRemoteMapper : BiMapper<String, LoungeData.Status> {
  override fun mapToRight(from: String): LoungeData.Status {
    return when (from) {
      LoungeRemote.STATUS_CREATED -> LoungeData.Status.CREATED
      LoungeRemote.STATUS_QUIT -> LoungeData.Status.QUIT
      LoungeRemote.STATUS_STARTED -> LoungeData.Status.STARTED
      LoungeRemote.STATUS_FINISHED -> LoungeData.Status.FINISHED
      else -> throw LoungeError.InvalidLoungeStatus
    }
  }

  override fun mapToLeft(from: LoungeData.Status): String {
    return when (from) {
      LoungeData.Status.CREATED -> LoungeRemote.STATUS_CREATED
      LoungeData.Status.QUIT -> LoungeRemote.STATUS_QUIT
      LoungeData.Status.STARTED -> LoungeRemote.STATUS_STARTED
      LoungeData.Status.FINISHED -> LoungeRemote.STATUS_FINISHED
    }
  }
}

internal fun String.asLoungeStatusData(): LoungeData.Status {
  return LoungeStatusRemoteMapper.mapToRight(this)
}

internal fun LoungeData.Status.asRemote(): String {
  return LoungeStatusRemoteMapper.mapToLeft(this)
}