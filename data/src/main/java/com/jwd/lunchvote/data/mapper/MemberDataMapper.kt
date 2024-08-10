package com.jwd.lunchvote.data.mapper

import com.jwd.lunchvote.core.common.mapper.BiMapper
import com.jwd.lunchvote.data.model.MemberData
import com.jwd.lunchvote.domain.entity.Member

private object MemberDataMapper : BiMapper<MemberData, Member> {
  override fun mapToRight(from: MemberData): Member =
    Member(
      loungeId = from.loungeId,
      userId = from.userId,
      userName = from.userName,
      userProfile = from.userProfile,
      type = from.type.asDomain(),
      status = from.status.asDomain(),
      createdAt = from.createdAt,
      deletedAt = from.deletedAt
    )

  override fun mapToLeft(from: Member): MemberData =
    MemberData(
      loungeId = from.loungeId,
      userId = from.userId,
      userName = from.userName,
      userProfile = from.userProfile,
      type = from.type.asData(),
      status = from.status.asData(),
      createdAt = from.createdAt,
      deletedAt = from.deletedAt
    )
}

private object MemberDataTypeMapper : BiMapper<MemberData.Type, Member.Type> {
  override fun mapToRight(from: MemberData.Type): Member.Type =
    when (from) {
      MemberData.Type.DEFAULT -> Member.Type.DEFAULT
      MemberData.Type.OWNER -> Member.Type.OWNER
      MemberData.Type.READY -> Member.Type.READY
      MemberData.Type.LEAVED -> Member.Type.LEAVED
      MemberData.Type.EXILED -> Member.Type.EXILED
    }

  override fun mapToLeft(from: Member.Type): MemberData.Type =
    when (from) {
      Member.Type.DEFAULT -> MemberData.Type.DEFAULT
      Member.Type.OWNER -> MemberData.Type.OWNER
      Member.Type.READY -> MemberData.Type.READY
      Member.Type.LEAVED -> MemberData.Type.LEAVED
      Member.Type.EXILED -> MemberData.Type.EXILED
    }
}

private object MemberDataStatusMapper : BiMapper<MemberData.Status, Member.Status> {
  override fun mapToRight(from: MemberData.Status): Member.Status =
    when (from) {
      MemberData.Status.STANDBY -> Member.Status.STANDBY
      MemberData.Status.VOTING -> Member.Status.VOTING
      MemberData.Status.VOTED -> Member.Status.VOTED
    }

  override fun mapToLeft(from: Member.Status): MemberData.Status =
    when (from) {
      Member.Status.STANDBY -> MemberData.Status.STANDBY
      Member.Status.VOTING -> MemberData.Status.VOTING
      Member.Status.VOTED -> MemberData.Status.VOTED
    }
}

internal fun MemberData.asDomain(): Member =
  MemberDataMapper.mapToRight(this)

internal fun Member.asData(): MemberData =
  MemberDataMapper.mapToLeft(this)

internal fun MemberData.Type.asDomain(): Member.Type =
  MemberDataTypeMapper.mapToRight(this)

internal fun Member.Type.asData(): MemberData.Type =
  MemberDataTypeMapper.mapToLeft(this)

internal fun MemberData.Status.asDomain(): Member.Status =
  MemberDataStatusMapper.mapToRight(this)

internal fun Member.Status.asData(): MemberData.Status =
  MemberDataStatusMapper.mapToLeft(this)