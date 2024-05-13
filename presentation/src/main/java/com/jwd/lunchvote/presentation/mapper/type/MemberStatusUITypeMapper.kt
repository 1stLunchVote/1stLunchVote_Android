package com.jwd.lunchvote.presentation.mapper.type

import com.jwd.lunchvote.core.common.mapper.BiMapper
import com.jwd.lunchvote.domain.entity.type.MemberStatusType
import com.jwd.lunchvote.presentation.model.type.MemberStatusUIType

private object MemberStatusUITypeMapper : BiMapper<MemberStatusUIType, MemberStatusType> {
  override fun mapToRight(from: MemberStatusUIType): MemberStatusType {
    return when (from) {
      MemberStatusUIType.OWNER -> MemberStatusType.OWNER
      MemberStatusUIType.JOINED -> MemberStatusType.JOINED
      MemberStatusUIType.READY -> MemberStatusType.READY
      MemberStatusUIType.EXILED -> MemberStatusType.EXILED
    }
  }

  override fun mapToLeft(from: MemberStatusType): MemberStatusUIType {
    return when (from) {
      MemberStatusType.OWNER -> MemberStatusUIType.OWNER
      MemberStatusType.JOINED -> MemberStatusUIType.JOINED
      MemberStatusType.READY -> MemberStatusUIType.READY
      MemberStatusType.EXILED -> MemberStatusUIType.EXILED
    }
  }
}

internal fun MemberStatusType.asUI(): MemberStatusUIType {
  return MemberStatusUITypeMapper.mapToLeft(this)
}

internal fun MemberStatusUIType.asDomain(): MemberStatusType {
  return MemberStatusUITypeMapper.mapToRight(this)
}