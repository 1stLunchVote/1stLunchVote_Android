package com.jwd.lunchvote.data.mapper

import com.jwd.lunchvote.core.common.mapper.BiMapper
import com.jwd.lunchvote.data.model.FriendData
import com.jwd.lunchvote.domain.entity.Friend

private object FriendDataMapper : BiMapper<FriendData, Friend> {
  override fun mapToRight(from: FriendData): Friend =
    Friend(
      id = from.id,
      userId = from.userId,
      friendId = from.friendId,
      createdAt = from.createdAt,
      matchedAt = from.matchedAt,
      deletedAt = from.deletedAt
    )

  override fun mapToLeft(from: Friend): FriendData =
    FriendData(
      id = from.id,
      userId = from.userId,
      friendId = from.friendId,
      createdAt = from.createdAt,
      matchedAt = from.matchedAt,
      deletedAt = from.deletedAt
    )
}

internal fun FriendData.asDomain(): Friend =
  FriendDataMapper.mapToRight(this)

internal fun Friend.asData(): FriendData =
  FriendDataMapper.mapToLeft(this)