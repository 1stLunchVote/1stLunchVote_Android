package com.jwd.lunchvote.remote.mapper

import com.jwd.lunchvote.core.common.mapper.BiMapper
import com.jwd.lunchvote.data.model.UserData
import com.jwd.lunchvote.remote.model.UserRemote

object UserRemoteMapper: BiMapper<UserRemote, UserData> {
  override fun mapToRight(from: UserRemote): UserData {
    return UserData(
      id = "",
      email = from.email,
      name = from.name,
      profileImageUrl = from.profileImageUrl,
      createdAt = from.createdAt
    )
  }

  override fun mapToLeft(from: UserData): UserRemote {
    return UserRemote(
      email = from.email,
      name = from.name,
      profileImageUrl = from.profileImageUrl,
      createdAt = from.createdAt
    )
  }
}

internal fun UserData.asRemote(): UserRemote {
  return UserRemoteMapper.mapToLeft(this)
}

internal fun UserRemote.asData(): UserData {
  return UserRemoteMapper.mapToRight(this)
}