package com.jwd.lunchvote.presentation.mapper

import com.jwd.lunchvote.core.common.mapper.BiMapper
import com.jwd.lunchvote.domain.entity.Member
import com.jwd.lunchvote.presentation.model.MemberUIModel
import com.jwd.lunchvote.presentation.util.toLong
import com.jwd.lunchvote.presentation.util.toZonedDateTime

private object MemberUIMapper : BiMapper<MemberUIModel, Member> {
  override fun mapToRight(from: MemberUIModel): Member =
    Member(
      loungeId = from.loungeId,
      userId = from.userId,
      userName = from.userName,
      userProfile = from.userProfile,
      type = from.type.asDomain(),
      createdAt = from.createdAt.toLong(),
      deletedAt = from.deletedAt?.toLong()
    )

  override fun mapToLeft(from: Member): MemberUIModel =
    MemberUIModel(
      loungeId = from.loungeId,
      userId = from.userId,
      userName = from.userName,
      userProfile = from.userProfile,
      type = from.type.asUI(),
      createdAt = from.createdAt.toZonedDateTime(),
      deletedAt = from.deletedAt?.toZonedDateTime()
    )
}

private object MemberUITypeMapper : BiMapper<MemberUIModel.Type, Member.Type> {
  override fun mapToRight(from: MemberUIModel.Type): Member.Type =
    when (from) {
      MemberUIModel.Type.DEFAULT -> Member.Type.DEFAULT
      MemberUIModel.Type.OWNER -> Member.Type.OWNER
      MemberUIModel.Type.READY -> Member.Type.READY
      MemberUIModel.Type.EXILED -> Member.Type.EXILED
    }

  override fun mapToLeft(from: Member.Type): MemberUIModel.Type =
    when (from) {
      Member.Type.DEFAULT -> MemberUIModel.Type.DEFAULT
      Member.Type.OWNER -> MemberUIModel.Type.OWNER
      Member.Type.READY -> MemberUIModel.Type.READY
      Member.Type.EXILED -> MemberUIModel.Type.EXILED
    }
}

internal fun MemberUIModel.asDomain(): Member =
  MemberUIMapper.mapToRight(this)

internal fun Member.asUI(): MemberUIModel =
  MemberUIMapper.mapToLeft(this)

internal fun MemberUIModel.Type.asDomain(): Member.Type =
  MemberUITypeMapper.mapToRight(this)

internal fun Member.Type.asUI(): MemberUIModel.Type =
  MemberUITypeMapper.mapToLeft(this)