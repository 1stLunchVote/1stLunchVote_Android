package com.jwd.lunchvote.presentation.mapper

import com.jwd.lunchvote.core.common.mapper.BiMapper
import com.jwd.lunchvote.domain.entity.User
import com.jwd.lunchvote.presentation.model.UserUIModel
import com.jwd.lunchvote.presentation.util.toLocalDateTime
import com.jwd.lunchvote.presentation.util.toLong

object UserUIMapper : BiMapper<UserUIModel, User> {
  override fun mapToRight(from: UserUIModel): User =
    User(
      id = from.id,
      email = from.email,
      name = from.name,
      profileImage = from.profileImage,
      createdAt = from.createdAt.toLong(),
      deletedAt = from.deletedAt?.toLong()
    )

  override fun mapToLeft(from: User): UserUIModel =
    UserUIModel(
      id = from.id,
      email = from.email,
      name = from.name,
      profileImage = from.profileImage,
      createdAt = from.createdAt.toLocalDateTime(),
      deletedAt = from.deletedAt?.toLocalDateTime()
    )
}


internal fun UserUIModel.asDomain(): User =
  UserUIMapper.mapToRight(this)

internal fun User.asUI(): UserUIModel =
  UserUIMapper.mapToLeft(this)