package com.jwd.lunchvote.data.mapper

import com.jwd.lunchvote.core.common.mapper.BiMapper
import com.jwd.lunchvote.data.mapper.type.asData
import com.jwd.lunchvote.data.mapper.type.asDomain
import com.jwd.lunchvote.data.model.LoungeChatData
import com.jwd.lunchvote.domain.entity.LoungeChat

private object LoungeChatDataMapper : BiMapper<LoungeChatData, LoungeChat> {
  override fun mapToRight(from: LoungeChatData): LoungeChat {
    return LoungeChat(
      id = from.id,
      loungeId = from.loungeId,
      userId = from.userId,
      userProfile = from.userProfile,
      message = from.message,
      messageType = from.messageType.asDomain(),
      createdAt = from.createdAt,
      sendStatus = from.sendStatus.asDomain()
    )
  }

  override fun mapToLeft(from: LoungeChat): LoungeChatData {
    return LoungeChatData(
      id = from.id,
      loungeId = from.loungeId,
      userId = from.userId,
      userProfile = from.userProfile,
      message = from.message,
      messageType = from.messageType.asData(),
      createdAt = from.createdAt,
      sendStatus = from.sendStatus.asData()
    )
  }
}

internal fun LoungeChatData.asDomain(): LoungeChat {
  return LoungeChatDataMapper.mapToRight(this)
}

internal fun LoungeChat.asData(): LoungeChatData {
  return LoungeChatDataMapper.mapToLeft(this)
}