package com.jwd.lunchvote.remote.mapper

import com.jwd.lunchvote.core.common.mapper.BiMapper
import com.jwd.lunchvote.data.model.FriendData
import com.jwd.lunchvote.remote.model.FriendRemote
import com.jwd.lunchvote.remote.util.toLong
import com.jwd.lunchvote.remote.util.toTimestamp

private object FriendRemoteMapper : BiMapper<FriendRemote, FriendData> {
  override fun mapToRight(from: FriendRemote): FriendData =
    FriendData(
      id = from.id,
      userId = from.userId,
      friendId = from.friendId,
      createdAt = from.createdAt.toLong(),
      matchedAt = from.matchedAt?.toLong(),
      deletedAt = from.deletedAt?.toLong()
    )

  override fun mapToLeft(from: FriendData): FriendRemote =
    FriendRemote(
      id = from.id,
      userId = from.userId,
      friendId = from.friendId,
      createdAt = from.createdAt.toTimestamp(),
      matchedAt = from.matchedAt?.toTimestamp(),
      deletedAt = from.deletedAt?.toTimestamp()
    )
}

internal fun FriendRemote.asData(): FriendData =
  FriendRemoteMapper.mapToRight(this)

internal fun FriendData.asRemote(): FriendRemote =
  FriendRemoteMapper.mapToLeft(this)