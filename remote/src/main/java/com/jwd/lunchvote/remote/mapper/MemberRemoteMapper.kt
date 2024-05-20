package com.jwd.lunchvote.remote.mapper

import com.jwd.lunchvote.core.common.error.LoungeError
import com.jwd.lunchvote.core.common.mapper.BiMapper
import com.jwd.lunchvote.data.model.MemberData
import com.jwd.lunchvote.remote.model.MemberRemote

private object MemberRemoteMapper : BiMapper<MemberRemote, MemberData> {
  override fun mapToRight(from: MemberRemote): MemberData {
    return MemberData(
      loungeId = from.loungeId,
      userId = "",
      userName = from.userName,
      userProfile = from.userProfile,
      type = from.type.asMemberTypeData(),
      createdAt = from.createdAt,
      deletedAt = from.deletedAt
    )
  }

  override fun mapToLeft(from: MemberData): MemberRemote {
    return MemberRemote(
      loungeId = from.loungeId,
      userName = from.userName,
      userProfile = from.userProfile,
      type = from.type.asRemote(),
      createdAt = from.createdAt,
      deletedAt = from.deletedAt
    )
  }
}

internal fun MemberData.asRemote(): MemberRemote {
  return MemberRemoteMapper.mapToLeft(this)
}

internal fun MemberRemote.asData(userId: String): MemberData {
  return MemberRemoteMapper.mapToRight(this).copy(userId = userId)
}

private object MemberTypeRemoteMapper : BiMapper<String, MemberData.Type> {
  override fun mapToRight(from: String): MemberData.Type {
    return when (from) {
      MemberRemote.TYPE_DEFAULT -> MemberData.Type.DEFAULT
      MemberRemote.TYPE_OWNER -> MemberData.Type.OWNER
      MemberRemote.TYPE_READY -> MemberData.Type.READY
      MemberRemote.TYPE_EXILED -> MemberData.Type.EXILED
      else -> throw LoungeError.InvalidMemberStatus
    }
  }

  override fun mapToLeft(from: MemberData.Type): String {
    return when (from) {
      MemberData.Type.DEFAULT -> MemberRemote.TYPE_DEFAULT
      MemberData.Type.OWNER -> MemberRemote.TYPE_OWNER
      MemberData.Type.READY -> MemberRemote.TYPE_READY
      MemberData.Type.EXILED -> MemberRemote.TYPE_EXILED
    }
  }
}

internal fun MemberData.Type.asRemote(): String {
  return MemberTypeRemoteMapper.mapToLeft(this)
}

internal fun String.asMemberTypeData(): MemberData.Type {
  return MemberTypeRemoteMapper.mapToRight(this)
}