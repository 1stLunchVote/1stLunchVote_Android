package com.jwd.lunchvote.remote.mapper

import com.jwd.lunchvote.mapper.BiMapper
import com.jwd.lunchvote.data.model.UserData
import com.jwd.lunchvote.remote.model.UserRemote
import com.jwd.lunchvote.remote.util.toLong
import com.jwd.lunchvote.remote.util.toTimestamp

private object UserRemoteMapper: BiMapper<UserRemote, UserData> {
  override fun mapToRight(from: UserRemote): UserData =
    UserData(
      id = "",
      email = from.email,
      name = from.name,
      profileImage = from.profileImage,
      createdAt = from.createdAt.toLong(),
      deletedAt = from.deletedAt?.toLong()
    )

  override fun mapToLeft(from: UserData): UserRemote =
    UserRemote(
      email = from.email,
      name = from.name,
      profileImage = from.profileImage,
      createdAt = from.createdAt.toTimestamp(),
      deletedAt = from.deletedAt?.toTimestamp()
    )
}

internal fun UserRemote.asData(id: String): UserData =
  UserRemoteMapper.mapToRight(this).copy(id = id)

internal fun UserData.asRemote(): UserRemote =
  UserRemoteMapper.mapToLeft(this)