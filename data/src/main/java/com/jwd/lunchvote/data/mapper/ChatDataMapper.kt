package com.jwd.lunchvote.data.mapper

import com.jwd.lunchvote.data.model.ChatData
import com.jwd.lunchvote.domain.entity.Chat
import com.jwd.lunchvote.mapper.BiMapper

private object ChatDataMapper : BiMapper<ChatData, Chat> {
  override fun mapToRight(from: ChatData): Chat =
    Chat(
      loungeId = from.loungeId,
      id = from.id,
      userId = from.userId,
      message = from.message,
      type = from.type.asDomain(),
      createdAt = from.createdAt
    )

  override fun mapToLeft(from: Chat): ChatData =
    ChatData(
      id = from.id,
      loungeId = from.loungeId,
      userId = from.userId,
      message = from.message,
      type = from.type.asData(),
      createdAt = from.createdAt
    )
}

private object ChatDataTypeMapper : BiMapper<ChatData.Type, Chat.Type> {
  override fun mapToRight(from: ChatData.Type): Chat.Type =
    when (from) {
      ChatData.Type.DEFAULT -> Chat.Type.DEFAULT
      ChatData.Type.SYSTEM -> Chat.Type.SYSTEM
    }

  override fun mapToLeft(from: Chat.Type): ChatData.Type =
    when (from) {
      Chat.Type.DEFAULT -> ChatData.Type.DEFAULT
      Chat.Type.SYSTEM -> ChatData.Type.SYSTEM
    }
}

internal fun ChatData.asDomain(): Chat =
  ChatDataMapper.mapToRight(this)

internal fun Chat.asData(): ChatData = ChatDataMapper.mapToLeft(this)

internal fun ChatData.Type.asDomain(): Chat.Type =
  ChatDataTypeMapper.mapToRight(this)

internal fun Chat.Type.asData(): ChatData.Type =
  ChatDataTypeMapper.mapToLeft(this)