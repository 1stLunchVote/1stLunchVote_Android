package com.jwd.lunchvote.presentation.mapper

import com.jwd.lunchvote.core.common.mapper.BiMapper
import com.jwd.lunchvote.domain.entity.Chat
import com.jwd.lunchvote.presentation.model.ChatUIModel
import com.jwd.lunchvote.presentation.util.toLocalDateTime
import com.jwd.lunchvote.presentation.util.toLong

private object ChatUIMapper: BiMapper<ChatUIModel, Chat> {
  override fun mapToRight(from: ChatUIModel): Chat {
    return Chat(
      loungeId = from.loungeId,
      id = from.id,
      userId = from.userId,
      userName = from.userName,
      userProfile = from.userProfile,
      message = from.message,
      type = from.type.asDomain(),
      createdAt = from.createdAt.toLong()
    )
  }

  override fun mapToLeft(from: Chat): ChatUIModel {
    return ChatUIModel(
      loungeId = from.loungeId,
      id = from.id,
      userId = from.userId,
      userName = from.userName,
      userProfile = from.userProfile,
      message = from.message,
      type = from.type.asUI(),
      createdAt = from.createdAt.toLocalDateTime()
    )
  }
}

internal fun ChatUIModel.asDomain(): Chat {
  return ChatUIMapper.mapToRight(this)
}

internal fun Chat.asUI(): ChatUIModel {
  return ChatUIMapper.mapToLeft(this)
}

private object MessageTypeUIMapper : BiMapper<ChatUIModel.Type, Chat.Type> {
  override fun mapToRight(from: ChatUIModel.Type): Chat.Type {
    return when (from) {
      ChatUIModel.Type.DEFAULT -> Chat.Type.DEFAULT
      ChatUIModel.Type.SYSTEM -> Chat.Type.SYSTEM
    }
  }

  override fun mapToLeft(from: Chat.Type): ChatUIModel.Type {
    return when (from) {
      Chat.Type.DEFAULT -> ChatUIModel.Type.DEFAULT
      Chat.Type.SYSTEM -> ChatUIModel.Type.SYSTEM
    }
  }
}

internal fun ChatUIModel.Type.asDomain(): Chat.Type {
  return MessageTypeUIMapper.mapToRight(this)
}

internal fun Chat.Type.asUI(): ChatUIModel.Type {
  return MessageTypeUIMapper.mapToLeft(this)
}