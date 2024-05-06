package com.jwd.lunchvote.data.mapper.type

import com.jwd.lunchvote.core.common.mapper.BiMapper
import com.jwd.lunchvote.data.model.type.MemberStatusDataType
import com.jwd.lunchvote.domain.entity.type.MemberStatusType

private object MemberStatusDataTypeMapper : BiMapper<MemberStatusDataType, MemberStatusType> {
  override fun mapToRight(from: MemberStatusDataType): MemberStatusType {
    return when (from) {
      MemberStatusDataType.OWNER -> MemberStatusType.OWNER
      MemberStatusDataType.JOINED -> MemberStatusType.JOINED
      MemberStatusDataType.READY -> MemberStatusType.READY
      MemberStatusDataType.EXILED -> MemberStatusType.EXILED
    }
  }

  override fun mapToLeft(from: MemberStatusType): MemberStatusDataType {
    return when (from) {
      MemberStatusType.OWNER -> MemberStatusDataType.OWNER
      MemberStatusType.JOINED -> MemberStatusDataType.JOINED
      MemberStatusType.READY -> MemberStatusDataType.READY
      MemberStatusType.EXILED -> MemberStatusDataType.EXILED
    }
  }
}

internal fun MemberStatusType.asData(): MemberStatusDataType {
  return MemberStatusDataTypeMapper.mapToLeft(this)
}

internal fun MemberStatusDataType.asDomain(): MemberStatusType {
  return MemberStatusDataTypeMapper.mapToRight(this)
}