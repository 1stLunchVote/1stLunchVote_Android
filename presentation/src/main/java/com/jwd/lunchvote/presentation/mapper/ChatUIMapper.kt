package com.jwd.lunchvote.presentation.mapper

import com.jwd.lunchvote.core.common.mapper.BiMapper
import com.jwd.lunchvote.domain.entity.LoungeChat
import com.jwd.lunchvote.presentation.model.ChatUIModel

private object ChatUIMapper: BiMapper<ChatUIModel, LoungeChat> {
  override fun mapToRight(from: ChatUIModel): LoungeChat {
    return LoungeChat(
      id = "",
      loungeId = "",
      userId = from.sender,
      userProfile = from.userProfile,
      message = from.message,
      messageType = from.messageType,
      sendStatus = from.sendStatus,
      createdAt = from.createdAt
    )
  }

  override fun mapToLeft(from: LoungeChat): ChatUIModel {
    return ChatUIModel(
      message = from.message,
      messageType = from.messageType,
      isMine = false,
      sender = from.userId,
      createdAt = from.createdAt,
      userProfile = from.userProfile,
      sendStatus = from.sendStatus
    )
  }
}

internal fun ChatUIModel.asDomain(): LoungeChat {
  return ChatUIMapper.mapToRight(this)
}

internal fun LoungeChat.asUI(): ChatUIModel {
  return ChatUIMapper.mapToLeft(this)
}