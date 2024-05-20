package com.jwd.lunchvote.data.mapper

import com.jwd.lunchvote.core.common.mapper.BiMapper
import com.jwd.lunchvote.data.model.MemberData
import com.jwd.lunchvote.domain.entity.Member

private object MemberDataMapper : BiMapper<MemberData, Member> {
  override fun mapToRight(from: MemberData): Member {
    return Member(
      loungeId = from.loungeId,
      userId = from.userId,
      userName = from.userName,
      userProfile = from.userProfile,
      type = from.type.asDomain(),
      createdAt = from.createdAt,
      deletedAt = from.deletedAt
    )
  }

  override fun mapToLeft(from: Member): MemberData {
    return MemberData(
      loungeId = from.loungeId,
      userId = from.userId,
      userName = from.userName,
      userProfile = from.userProfile,
      type = from.type.asData(),
      createdAt = from.createdAt,
      deletedAt = from.deletedAt
    )
  }
}

internal fun Member.asData(): MemberData {
  return MemberDataMapper.mapToLeft(this)
}

internal fun MemberData.asDomain(): Member {
  return MemberDataMapper.mapToRight(this)
}

private object MemberTypeDataMapper : BiMapper<MemberData.Type, Member.Type> {
  override fun mapToRight(from: MemberData.Type): Member.Type {
    return when (from) {
      MemberData.Type.DEFAULT -> Member.Type.DEFAULT
      MemberData.Type.OWNER -> Member.Type.OWNER
      MemberData.Type.READY -> Member.Type.READY
      MemberData.Type.EXILED -> Member.Type.EXILED
    }
  }

  override fun mapToLeft(from: Member.Type): MemberData.Type {
    return when (from) {
      Member.Type.DEFAULT -> MemberData.Type.DEFAULT
      Member.Type.OWNER -> MemberData.Type.OWNER
      Member.Type.READY -> MemberData.Type.READY
      Member.Type.EXILED -> MemberData.Type.EXILED
    }
  }
}

internal fun Member.Type.asData(): MemberData.Type {
  return MemberTypeDataMapper.mapToLeft(this)
}

internal fun MemberData.Type.asDomain(): Member.Type {
  return MemberTypeDataMapper.mapToRight(this)
}