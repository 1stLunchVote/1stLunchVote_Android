package com.jwd.lunchvote.data.mapper

import com.jwd.lunchvote.core.common.mapper.BiMapper
import com.jwd.lunchvote.data.model.ChatData
import com.jwd.lunchvote.domain.entity.Chat

private object ChatDataMapper : BiMapper<ChatData, Chat> {
  override fun mapToRight(from: ChatData): Chat {
    return Chat(
      loungeId = from.loungeId,
      id = from.id,
      userId = from.userId,
      userName = from.userName,
      userProfile = from.userProfile,
      message = from.message,
      type = from.type.asDomain(),
      createdAt = from.createdAt
    )
  }

  override fun mapToLeft(from: Chat): ChatData {
    return ChatData(
      id = from.id,
      loungeId = from.loungeId,
      userId = from.userId,
      userName = from.userName,
      userProfile = from.userProfile,
      message = from.message,
      type = from.type.asData(),
      createdAt = from.createdAt
    )
  }
}

internal fun ChatData.asDomain(): Chat {
  return ChatDataMapper.mapToRight(this)
}

internal fun Chat.asData(): ChatData {
  return ChatDataMapper.mapToLeft(this)
}

private object MessageTypeDataMapper : BiMapper<ChatData.Type, Chat.Type> {
  override fun mapToRight(from: ChatData.Type): Chat.Type {
    return when (from) {
      ChatData.Type.DEFAULT -> Chat.Type.DEFAULT
      ChatData.Type.SYSTEM -> Chat.Type.SYSTEM
    }
  }

  override fun mapToLeft(from: Chat.Type): ChatData.Type {
    return when (from) {
      Chat.Type.DEFAULT -> ChatData.Type.DEFAULT
      Chat.Type.SYSTEM -> ChatData.Type.SYSTEM
    }
  }
}

internal fun ChatData.Type.asDomain(): Chat.Type {
  return MessageTypeDataMapper.mapToRight(this)
}

internal fun Chat.Type.asData(): ChatData.Type {
  return MessageTypeDataMapper.mapToLeft(this)
}