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
      MemberData.Type.EXILED -> Member.Type.EXILED
    }

  override fun mapToLeft(from: Member.Type): MemberData.Type =
    when (from) {
      Member.Type.DEFAULT -> MemberData.Type.DEFAULT
      Member.Type.OWNER -> MemberData.Type.OWNER
      Member.Type.READY -> MemberData.Type.READY
      Member.Type.EXILED -> MemberData.Type.EXILED
    }
}

internal fun MemberData.asDomain(): Member =
  MemberDataMapper.mapToRight(this)

internal fun Member.asData(): MemberData =
  MemberDataMapper.mapToLeft(this)

internal fun Member.Type.asData(): MemberData.Type {
  return MemberDataTypeMapper.mapToLeft(this)
}

internal fun MemberData.Type.asDomain(): Member.Type {
  return MemberDataTypeMapper.mapToRight(this)
}