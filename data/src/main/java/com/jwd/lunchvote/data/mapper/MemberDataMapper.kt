package com.jwd.lunchvote.data.mapper

import com.jwd.lunchvote.core.common.mapper.BiMapper
import com.jwd.lunchvote.data.mapper.type.asData
import com.jwd.lunchvote.data.mapper.type.asDomain
import com.jwd.lunchvote.data.model.MemberData
import com.jwd.lunchvote.domain.entity.Member

private object MemberDataMapper : BiMapper<MemberData, Member> {
  override fun mapToRight(from: MemberData): Member {
    return Member(
      userId = from.userId,
      userProfile = from.userProfile,
      loungeId = from.loungeId,
      status = from.status.asDomain(),
      joinedAt = from.joinedAt
    )
  }

  override fun mapToLeft(from: Member): MemberData {
    return MemberData(
      userId = from.userId,
      userProfile = from.userProfile,
      loungeId = from.loungeId,
      status = from.status.asData(),
      joinedAt = from.joinedAt
    )
  }
}

internal fun Member.asData(): MemberData {
  return MemberDataMapper.mapToLeft(this)
}

internal fun MemberData.asDomain(): Member {
  return MemberDataMapper.mapToRight(this)
}