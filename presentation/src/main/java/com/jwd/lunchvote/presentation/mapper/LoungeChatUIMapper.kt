package com.jwd.lunchvote.presentation.mapper

import com.jwd.lunchvote.core.common.mapper.BiMapper
import com.jwd.lunchvote.domain.entity.LoungeChat
import com.jwd.lunchvote.presentation.mapper.type.asDomain
import com.jwd.lunchvote.presentation.mapper.type.asUI
import com.jwd.lunchvote.presentation.model.LoungeChatUIModel

private object ChatUIMapper: BiMapper<LoungeChatUIModel, LoungeChat> {
  override fun mapToRight(from: LoungeChatUIModel): LoungeChat {
    return LoungeChat(
      id = from.id,
      loungeId = from.loungeId,
      userId = from.userId,
      userName = from.userName,
      userProfile = from.userProfile,
      message = from.message,
      messageType = from.messageType.asDomain(),
      sendStatus = from.sendStatus.asDomain(),
      createdAt = from.createdAt
    )
  }

  override fun mapToLeft(from: LoungeChat): LoungeChatUIModel {
    return LoungeChatUIModel(
      id = from.id,
      loungeId = from.loungeId,
      userId = from.userId,
      userName = from.userName,
      userProfile = from.userProfile,
      message = from.message,
      messageType = from.messageType.asUI(),
      sendStatus = from.sendStatus.asUI(),
      createdAt = from.createdAt
    )
  }
}

internal fun LoungeChatUIModel.asDomain(): LoungeChat {
  return ChatUIMapper.mapToRight(this)
}

internal fun LoungeChat.asUI(): LoungeChatUIModel {
  return ChatUIMapper.mapToLeft(this)
}