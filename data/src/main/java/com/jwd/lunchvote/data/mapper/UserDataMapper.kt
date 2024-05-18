package com.jwd.lunchvote.data.mapper

import com.jwd.lunchvote.core.common.mapper.BiMapper
import com.jwd.lunchvote.data.model.UserData
import com.jwd.lunchvote.domain.entity.User

object UserDataMapper: BiMapper<UserData, User> {
  override fun mapToRight(from: UserData): User {
    return User(
      id = from.id,
      email = from.email,
      name = from.name,
      profileImage = from.profileImage,
      createdAt = from.createdAt,
      deletedAt = from.deletedAt
    )
  }

  override fun mapToLeft(from: User): UserData {
    return UserData(
      id = from.id,
      email = from.email,
      name = from.name,
      profileImage = from.profileImage,
      createdAt = from.createdAt,
      deletedAt = from.deletedAt
    )
  }
}

internal fun User.asData(): UserData {
  return UserDataMapper.mapToLeft(this)
}

internal fun UserData.asDomain(): User {
  return UserDataMapper.mapToRight(this)
}