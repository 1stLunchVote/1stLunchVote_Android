package com.jwd.lunchvote.remote.mapper

import com.jwd.lunchvote.core.common.mapper.BiMapper
import com.jwd.lunchvote.data.model.LoungeData
import com.jwd.lunchvote.data.model.MemberData
import com.jwd.lunchvote.remote.mapper.type.asLoungeStatusDataType
import com.jwd.lunchvote.remote.mapper.type.asRemote
import com.jwd.lunchvote.remote.model.LoungeRemote

private object LoungeRemoteMapper: BiMapper<LoungeRemote, LoungeData> {
  override fun mapToRight(from: LoungeRemote): LoungeData {
    return LoungeData(
      id = "",
      status = from.status.asLoungeStatusDataType(),
      members = emptyList()
    )
  }

  override fun mapToLeft(from: LoungeData): LoungeRemote {
    return LoungeRemote(
      status = from.status.asRemote()
    )
  }
}

internal fun LoungeRemote.asData(id: String, members: List<MemberData>): LoungeData {
  return LoungeRemoteMapper.mapToRight(this).copy(id = id, members = members)
}

internal fun LoungeData.asRemote(): LoungeRemote {
  return LoungeRemoteMapper.mapToLeft(this)
}