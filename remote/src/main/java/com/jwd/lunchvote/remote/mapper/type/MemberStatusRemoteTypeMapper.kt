package com.jwd.lunchvote.remote.mapper.type

import com.jwd.lunchvote.core.common.error.LoungeError
import com.jwd.lunchvote.core.common.mapper.BiMapper
import com.jwd.lunchvote.data.model.type.MemberStatusDataType

private object MemberStatusRemoteTypeMapper : BiMapper<String, MemberStatusDataType> {
  override fun mapToRight(from: String): MemberStatusDataType {
    return when (from) {
      "owner" -> MemberStatusDataType.OWNER
      "joined" -> MemberStatusDataType.JOINED
      "ready" -> MemberStatusDataType.READY
      "exiled" -> MemberStatusDataType.EXILED
      else -> throw LoungeError.InvalidMemberStatus
    }
  }

  override fun mapToLeft(from: MemberStatusDataType): String {
    return when (from) {
      MemberStatusDataType.OWNER -> "owner"
      MemberStatusDataType.JOINED -> "joined"
      MemberStatusDataType.READY -> "ready"
      MemberStatusDataType.EXILED -> "exiled"
    }
  }
}

internal fun MemberStatusDataType.asRemote(): String {
  return MemberStatusRemoteTypeMapper.mapToLeft(this)
}

internal fun String.asMemberStatusDataType(): MemberStatusDataType {
  return MemberStatusRemoteTypeMapper.mapToRight(this)
}