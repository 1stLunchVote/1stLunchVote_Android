package com.jwd.lunchvote.remote.mapper

import com.jwd.lunchvote.core.common.mapper.BiMapper
import com.jwd.lunchvote.data.model.UserData
import com.jwd.lunchvote.remote.model.UserRemote
import com.jwd.lunchvote.remote.util.toLong
import com.jwd.lunchvote.remote.util.toTimestamp

private object UserRemoteMapper: BiMapper<UserRemote, UserData> {
  override fun mapToRight(from: UserRemote): UserData {
    return UserData(
      id = "",
      email = from.email,
      name = from.name,
      profileImage = from.profileImage,
      createdAt = from.createdAt.toLong(),
      deletedAt = from.deletedAt?.toLong()
    )
  }

  override fun mapToLeft(from: UserData): UserRemote {
    return UserRemote(
      email = from.email,
      name = from.name,
      profileImage = from.profileImage,
      createdAt = from.createdAt.toTimestamp(),
      deletedAt = from.deletedAt?.toTimestamp()
    )
  }
}

internal fun UserData.asRemote(): UserRemote {
  return UserRemoteMapper.mapToLeft(this)
}

internal fun UserRemote.asData(id: String): UserData {
  return UserRemoteMapper.mapToRight(this).copy(id = id)
}