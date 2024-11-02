package com.jwd.lunchvote.remote.mapper

import kr.co.inbody.library.mapper.BiMapper
import com.jwd.lunchvote.data.model.FriendData
import com.jwd.lunchvote.remote.model.FriendRemote
import com.jwd.lunchvote.remote.util.toLong
import com.jwd.lunchvote.remote.util.toTimestamp

private object FriendRemoteMapper : BiMapper<FriendRemote, FriendData> {
  override fun mapToRight(from: FriendRemote): FriendData =
    FriendData(
      id = "",
      userId = from.userId,
      friendId = from.friendId,
      createdAt = from.createdAt.toLong(),
      matchedAt = from.matchedAt?.toLong(),
      deletedAt = from.deletedAt?.toLong()
    )

  override fun mapToLeft(from: FriendData): FriendRemote =
    FriendRemote(
      userId = from.userId,
      friendId = from.friendId,
      createdAt = from.createdAt.toTimestamp(),
      matchedAt = from.matchedAt?.toTimestamp(),
      deletedAt = from.deletedAt?.toTimestamp()
    )
}

internal fun FriendRemote.asData(id: String): FriendData =
  FriendRemoteMapper.mapToRight(this).copy(id = id)

internal fun FriendData.asRemote(): FriendRemote =
  FriendRemoteMapper.mapToLeft(this)