package com.jwd.lunchvote.remote.mapper

import com.jwd.lunchvote.core.common.mapper.BiMapper
import com.jwd.lunchvote.data.model.MemberData
import com.jwd.lunchvote.remote.mapper.type.asMemberStatusDataType
import com.jwd.lunchvote.remote.mapper.type.asRemote
import com.jwd.lunchvote.remote.model.MemberRemote

private object MemberRemoteMapper : BiMapper<MemberRemote, MemberData> {
  override fun mapToRight(from: MemberRemote): MemberData {
    return MemberData(
      userId = "",
      userName = from.userName,
      userProfile = from.userProfile,
      loungeId = from.loungeId,
      status = from.status.asMemberStatusDataType(),
      joinedAt = from.joinedAt
    )
  }

  override fun mapToLeft(from: MemberData): MemberRemote {
    return MemberRemote(
      userName = from.userName,
      userProfile = from.userProfile,
      loungeId = from.loungeId,
      status = from.status.asRemote(),
      joinedAt = from.joinedAt
    )
  }
}

internal fun MemberData.asRemote(): MemberRemote {
  return MemberRemoteMapper.mapToLeft(this)
}

internal fun MemberRemote.asData(userId: String): MemberData {
  return MemberRemoteMapper.mapToRight(this).copy(userId = userId)
}