package com.jwd.lunchvote.remote.mapper

import com.jwd.lunchvote.core.common.error.LoungeError
import com.jwd.lunchvote.core.common.mapper.BiMapper
import com.jwd.lunchvote.data.model.MemberData
import com.jwd.lunchvote.remote.model.MemberRemote

private object MemberRemoteMapper : BiMapper<MemberRemote, MemberData> {
  override fun mapToRight(from: MemberRemote): MemberData =
    MemberData(
      loungeId = from.loungeId,
      userId = "",
      userName = from.userName,
      userProfile = from.userProfile,
      type = from.type.asMemberDataType(),
      createdAt = from.createdAt,
      deletedAt = from.deletedAt
    )

  override fun mapToLeft(from: MemberData): MemberRemote =
    MemberRemote(
      loungeId = from.loungeId,
      userName = from.userName,
      userProfile = from.userProfile,
      type = from.type.asRemote(),
      createdAt = from.createdAt,
      deletedAt = from.deletedAt
    )
}

private object MemberRemoteTypeMapper : BiMapper<String, MemberData.Type> {
  override fun mapToRight(from: String): MemberData.Type =
    when (from) {
      MemberRemote.TYPE_DEFAULT -> MemberData.Type.DEFAULT
      MemberRemote.TYPE_OWNER -> MemberData.Type.OWNER
      MemberRemote.TYPE_READY -> MemberData.Type.READY
      MemberRemote.TYPE_EXILED -> MemberData.Type.EXILED
      else -> throw LoungeError.InvalidMemberType
    }

  override fun mapToLeft(from: MemberData.Type): String =
    when (from) {
      MemberData.Type.DEFAULT -> MemberRemote.TYPE_DEFAULT
      MemberData.Type.OWNER -> MemberRemote.TYPE_OWNER
      MemberData.Type.READY -> MemberRemote.TYPE_READY
      MemberData.Type.EXILED -> MemberRemote.TYPE_EXILED
    }
}

internal fun MemberRemote.asData(userId: String): MemberData =
  MemberRemoteMapper.mapToRight(this).copy(userId = userId)

internal fun MemberData.asRemote(): MemberRemote =
  MemberRemoteMapper.mapToLeft(this)

internal fun String.asMemberDataType(): MemberData.Type =
  MemberRemoteTypeMapper.mapToRight(this)

internal fun MemberData.Type.asRemote(): String =
  MemberRemoteTypeMapper.mapToLeft(this)