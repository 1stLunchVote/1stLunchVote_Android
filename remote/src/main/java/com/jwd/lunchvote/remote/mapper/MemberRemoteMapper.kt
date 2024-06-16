package com.jwd.lunchvote.remote.mapper

import com.jwd.lunchvote.core.common.mapper.BiMapper
import com.jwd.lunchvote.data.model.MemberData
import com.jwd.lunchvote.remote.model.MemberRemote
import kr.co.inbody.config.error.LoungeError

private object MemberRemoteMapper : BiMapper<MemberRemote, MemberData> {
  override fun mapToRight(from: MemberRemote): MemberData =
    MemberData(
      loungeId = from.loungeId,
      userId = "",
      userName = from.userName,
      userProfile = from.userProfile,
      type = from.type.asMemberDataType(),
      status = from.status.asMemberDataStatus(),
      createdAt = from.createdAt,
      deletedAt = from.deletedAt
    )

  override fun mapToLeft(from: MemberData): MemberRemote =
    MemberRemote(
      loungeId = from.loungeId,
      userName = from.userName,
      userProfile = from.userProfile,
      type = from.type.asRemote(),
      status = from.status.asRemote(),
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

private object MemberRemoteStatusMapper : BiMapper<String, MemberData.Status> {
  override fun mapToRight(from: String): MemberData.Status =
    when (from) {
      MemberRemote.STATUS_STANDBY -> MemberData.Status.STANDBY
      MemberRemote.STATUS_VOTING -> MemberData.Status.VOTING
      MemberRemote.STATUS_VOTED -> MemberData.Status.VOTED
      else -> throw LoungeError.InvalidMemberStatus
    }

  override fun mapToLeft(from: MemberData.Status): String =
    when (from) {
      MemberData.Status.STANDBY -> MemberRemote.STATUS_STANDBY
      MemberData.Status.VOTING -> MemberRemote.STATUS_VOTING
      MemberData.Status.VOTED -> MemberRemote.STATUS_VOTED
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

internal fun String.asMemberDataStatus(): MemberData.Status =
  MemberRemoteStatusMapper.mapToRight(this)

internal fun MemberData.Status.asRemote(): String =
  MemberRemoteStatusMapper.mapToLeft(this)