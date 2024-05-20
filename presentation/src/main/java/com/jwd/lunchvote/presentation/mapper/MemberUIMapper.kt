package com.jwd.lunchvote.presentation.mapper

import com.jwd.lunchvote.core.common.mapper.BiMapper
import com.jwd.lunchvote.domain.entity.Member
import com.jwd.lunchvote.presentation.model.MemberUIModel
import com.jwd.lunchvote.presentation.util.toLocalDateTime
import com.jwd.lunchvote.presentation.util.toLong

private object MemberUIMapper : BiMapper<MemberUIModel, Member> {
  override fun mapToRight(from: MemberUIModel): Member {
    return Member(
      loungeId = from.loungeId,
      userId = from.userId,
      userName = from.userName,
      userProfile = from.userProfile,
      type = from.type.asDomain(),
      createdAt = from.createdAt.toLong(),
      deletedAt = from.deletedAt?.toLong()
    )
  }

  override fun mapToLeft(from: Member): MemberUIModel {
    return MemberUIModel(
      loungeId = from.loungeId,
      userId = from.userId,
      userName = from.userName,
      userProfile = from.userProfile,
      type = from.type.asUI(),
      createdAt = from.createdAt.toLocalDateTime(),
      deletedAt = from.deletedAt?.toLocalDateTime()
    )
  }

}

internal fun Member.asUI(): MemberUIModel {
  return MemberUIMapper.mapToLeft(this)
}

internal fun MemberUIModel.asDomain(): Member {
  return MemberUIMapper.mapToRight(this)
}

private object MemberTypeUIMapper : BiMapper<MemberUIModel.Type, Member.Type> {
  override fun mapToRight(from: MemberUIModel.Type): Member.Type {
    return when (from) {
      MemberUIModel.Type.DEFAULT -> Member.Type.DEFAULT
      MemberUIModel.Type.OWNER -> Member.Type.OWNER
      MemberUIModel.Type.READY -> Member.Type.READY
      MemberUIModel.Type.EXILED -> Member.Type.EXILED
    }
  }

  override fun mapToLeft(from: Member.Type): MemberUIModel.Type {
    return when (from) {
      Member.Type.DEFAULT -> MemberUIModel.Type.DEFAULT
      Member.Type.OWNER -> MemberUIModel.Type.OWNER
      Member.Type.READY -> MemberUIModel.Type.READY
      Member.Type.EXILED -> MemberUIModel.Type.EXILED
    }
  }
}

internal fun Member.Type.asUI(): MemberUIModel.Type {
  return MemberTypeUIMapper.mapToLeft(this)
}

internal fun MemberUIModel.Type.asDomain(): Member.Type {
  return MemberTypeUIMapper.mapToRight(this)
}