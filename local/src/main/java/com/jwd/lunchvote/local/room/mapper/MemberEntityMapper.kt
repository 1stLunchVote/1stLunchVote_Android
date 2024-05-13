package com.jwd.lunchvote.local.room.mapper

import com.jwd.lunchvote.core.common.mapper.BiMapper
import com.jwd.lunchvote.data.model.MemberData
import com.jwd.lunchvote.local.room.entity.MemberEntity

private object MemberEntityMapper : BiMapper<MemberEntity, MemberData> {
  override fun mapToRight(from: MemberEntity): MemberData {
    return MemberData(
      userId = from.userId,
      userName = from.userName,
      userProfile = from.userProfile,
      loungeId = from.loungeId,
      status = from.status,
      joinedAt = from.joinedAt
    )
  }

  override fun mapToLeft(from: MemberData): MemberEntity {
    return MemberEntity(
      userId = from.userId,
      userName = from.userName,
      userProfile = from.userProfile,
      loungeId = from.loungeId,
      status = from.status,
      joinedAt = from.joinedAt
    )
  }
}

internal fun MemberEntity.asData(): MemberData {
  return MemberEntityMapper.mapToRight(this)
}

internal fun MemberData.asEntity(): MemberEntity {
  return MemberEntityMapper.mapToLeft(this)
}