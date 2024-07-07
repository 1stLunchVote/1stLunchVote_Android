package com.jwd.lunchvote.remote.mapper

import com.jwd.lunchvote.core.common.mapper.BiMapper
import com.jwd.lunchvote.data.model.UserStatusData
import com.jwd.lunchvote.remote.model.UserStatusRemote

private object UserStatusRemoteMapper : BiMapper<UserStatusRemote, UserStatusData> {
  override fun mapToRight(from: UserStatusRemote): UserStatusData =
    UserStatusData(
      userId = "",
      lastOnline = from.lastOnline,
      loungeId = from.loungeId
    )

  override fun mapToLeft(from: UserStatusData): UserStatusRemote =
    UserStatusRemote(
      lastOnline = from.lastOnline,
      loungeId = from.loungeId
    )
}

internal fun UserStatusRemote.asData(userId: String): UserStatusData =
  UserStatusRemoteMapper.mapToRight(this).copy(userId = userId)

internal fun UserStatusData.asRemote(): UserStatusRemote =
  UserStatusRemoteMapper.mapToLeft(this)