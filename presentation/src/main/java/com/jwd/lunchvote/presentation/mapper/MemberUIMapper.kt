package com.jwd.lunchvote.presentation.mapper

import com.jwd.lunchvote.core.common.mapper.BiMapper
import com.jwd.lunchvote.domain.entity.Member
import com.jwd.lunchvote.presentation.mapper.type.asDomain
import com.jwd.lunchvote.presentation.mapper.type.asUI
import com.jwd.lunchvote.presentation.model.MemberUIModel

private object MemberUIMapper : BiMapper<MemberUIModel, Member> {
  override fun mapToRight(from: MemberUIModel): Member {
    return Member(
      userId = from.userId,
      userProfile = from.userProfile,
      loungeId = from.loungeId,
      status = from.status.asDomain(),
      joinedAt = from.joinedAt
    )
  }

  override fun mapToLeft(from: Member): MemberUIModel {
    return MemberUIModel(
      userId = from.userId,
      userProfile = from.userProfile,
      loungeId = from.loungeId,
      status = from.status.asUI(),
      joinedAt = from.joinedAt
    )
  }

}

internal fun Member.asUI(): MemberUIModel {
  return MemberUIMapper.mapToLeft(this)
}

internal fun MemberUIModel.asDomain(): Member {
  return MemberUIMapper.mapToRight(this)
}