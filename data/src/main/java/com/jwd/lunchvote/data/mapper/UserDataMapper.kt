package com.jwd.lunchvote.data.mapper

import kr.co.inbody.library.mapper.BiMapper
import com.jwd.lunchvote.data.model.UserData
import com.jwd.lunchvote.domain.entity.User

private object UserDataMapper: BiMapper<UserData, User> {
  override fun mapToRight(from: UserData): User =
    User(
      id = from.id,
      email = from.email,
      name = from.name,
      profileImage = from.profileImage,
      createdAt = from.createdAt,
      deletedAt = from.deletedAt
    )

  override fun mapToLeft(from: User): UserData =
    UserData(
      id = from.id,
      email = from.email,
      name = from.name,
      profileImage = from.profileImage,
      createdAt = from.createdAt,
      deletedAt = from.deletedAt
    )
}

internal fun UserData.asDomain(): User =
  UserDataMapper.mapToRight(this)

internal fun User.asData(): UserData =
  UserDataMapper.mapToLeft(this)