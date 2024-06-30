package com.jwd.lunchvote.presentation.mapper

import com.jwd.lunchvote.core.common.mapper.BiMapper
import com.jwd.lunchvote.domain.entity.Friend
import com.jwd.lunchvote.presentation.model.FriendUIModel
import com.jwd.lunchvote.presentation.util.toLong
import com.jwd.lunchvote.presentation.util.toZonedDateTime

private object FriendUiMapper : BiMapper<FriendUIModel, Friend> {
  override fun mapToRight(from: FriendUIModel): Friend =
    Friend(
      id = from.id,
      userId = from.userId,
      friendId = from.friendId,
      createdAt = from.createdAt.toLong(),
      matchedAt = from.matchedAt?.toLong(),
      deletedAt = from.deletedAt?.toLong()
    )

  override fun mapToLeft(from: Friend): FriendUIModel =
    FriendUIModel(
      id = from.id,
      userId = from.userId,
      friendId = from.friendId,
      createdAt = from.createdAt.toZonedDateTime(),
      matchedAt = from.matchedAt?.toZonedDateTime(),
      deletedAt = from.deletedAt?.toZonedDateTime()
    )
}

internal fun FriendUIModel.asDomain(): Friend =
  FriendUiMapper.mapToRight(this)

internal fun Friend.asUI(): FriendUIModel =
  FriendUiMapper.mapToLeft(this)