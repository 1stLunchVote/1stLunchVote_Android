package com.jwd.lunchvote.data.mapper

import com.jwd.lunchvote.core.common.mapper.BiMapper
import com.jwd.lunchvote.data.model.UserStatusData
import com.jwd.lunchvote.domain.entity.UserStatus

private object UserStatusDataMapper : BiMapper<UserStatusData, UserStatus> {
  override fun mapToRight(from: UserStatusData): UserStatus =
    UserStatus(
      userId = from.userId,
      lastOnline = from.lastOnline,
      loungeId = from.loungeId
    )

  override fun mapToLeft(from: UserStatus): UserStatusData =
    UserStatusData(
      userId = from.userId,
      lastOnline = from.lastOnline,
      loungeId = from.loungeId
    )
}

internal fun UserStatusData.asDomain(): UserStatus =
  UserStatusDataMapper.mapToRight(this)

internal fun UserStatus.asData(): UserStatusData =
  UserStatusDataMapper.mapToLeft(this)
