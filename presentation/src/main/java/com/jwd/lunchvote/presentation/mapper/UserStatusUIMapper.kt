package com.jwd.lunchvote.presentation.mapper

import com.jwd.lunchvote.domain.entity.UserStatus
import com.jwd.lunchvote.mapper.BiMapper
import com.jwd.lunchvote.presentation.model.UserStatusUIModel
import com.jwd.lunchvote.presentation.util.toLong
import com.jwd.lunchvote.presentation.util.toZonedDateTime

private object UserStatusUIMapper : BiMapper<UserStatusUIModel, UserStatus> {
  override fun mapToRight(from: UserStatusUIModel): UserStatus =
    UserStatus(
      userId = from.userId,
      lastOnline = from.lastOnline?.toLong(),
      loungeId = from.loungeId
    )

  override fun mapToLeft(from: UserStatus): UserStatusUIModel =
    UserStatusUIModel(
      userId = from.userId,
      lastOnline = from.lastOnline?.toZonedDateTime(),
      loungeId = from.loungeId
    )
}

internal fun UserStatusUIModel.asDomain(): UserStatus =
  UserStatusUIMapper.mapToRight(this)

internal fun UserStatus.asUI(): UserStatusUIModel =
  UserStatusUIMapper.mapToLeft(this)