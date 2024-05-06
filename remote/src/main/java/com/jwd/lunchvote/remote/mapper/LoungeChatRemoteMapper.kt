package com.jwd.lunchvote.remote.mapper

import com.jwd.lunchvote.core.common.mapper.BiMapper
import com.jwd.lunchvote.core.common.mapper.Mapper
import com.jwd.lunchvote.data.model.LoungeChatData
import com.jwd.lunchvote.data.model.type.SendStatusDataType
import com.jwd.lunchvote.remote.mapper.type.asMessageDataType
import com.jwd.lunchvote.remote.mapper.type.asRemote
import com.jwd.lunchvote.remote.model.LoungeChatRemote

private object LoungeChatRemoteMapper : BiMapper<LoungeChatRemote, LoungeChatData> {
  override fun mapToRight(from: LoungeChatRemote): LoungeChatData {
    return LoungeChatData(
      id = "",
      loungeId = from.loungeId.orEmpty(),
      userId = from.userId.orEmpty(),
      userProfile = from.userProfile,
      message = from.message.orEmpty(),
      messageType = from.messageType.asMessageDataType(),
      createdAt = from.createdAt.orEmpty(),
      sendStatus = SendStatusDataType.SUCCESS
    )
  }

  override fun mapToLeft(from: LoungeChatData): LoungeChatRemote {
    return LoungeChatRemote(
      loungeId = from.loungeId,
      userId = from.userId,
      userProfile = from.userProfile,
      message = from.message,
      messageType = from.messageType.asRemote(),
      createdAt = from.createdAt
    )
  }
}

internal fun LoungeChatRemote.asData(): LoungeChatData {
  return LoungeChatRemoteMapper.mapToRight(this)
}

internal fun LoungeChatData.asRemote(): LoungeChatRemote {
  return LoungeChatRemoteMapper.mapToLeft(this)
}