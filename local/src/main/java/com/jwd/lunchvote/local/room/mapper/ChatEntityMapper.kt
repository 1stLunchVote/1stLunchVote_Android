package com.jwd.lunchvote.local.room.mapper

import com.jwd.lunchvote.core.common.mapper.BiMapper
import com.jwd.lunchvote.data.model.ChatData
import com.jwd.lunchvote.local.room.entity.ChatEntity

private object ChatEntityMapper : BiMapper<ChatEntity, ChatData> {
  override fun mapToRight(from: ChatEntity): ChatData {
    return ChatData(
      id = from.id,
      userId = from.userId,
      loungeId = from.loungeId,
      message = from.message,
      type = from.type.asData(),
      createdAt = from.createdAt
    )
  }

  override fun mapToLeft(from: ChatData): ChatEntity {
    return ChatEntity(
      id = from.id,
      userId = from.userId,
      loungeId = from.loungeId,
      message = from.message,
      type = from.type.asEntity(),
      createdAt = from.createdAt
    )
  }
}

internal fun ChatEntity.asData(): ChatData {
  return ChatEntityMapper.mapToRight(this)
}

internal fun ChatData.asEntity(): ChatEntity {
  return ChatEntityMapper.mapToLeft(this)
}

private object ChatTypeLocalMapper : BiMapper<ChatEntity.Type, ChatData.Type> {
  override fun mapToRight(from: ChatEntity.Type): ChatData.Type {
    return when (from) {
      ChatEntity.Type.DEFAULT -> ChatData.Type.DEFAULT
      ChatEntity.Type.SYSTEM -> ChatData.Type.SYSTEM
    }
  }

  override fun mapToLeft(from: ChatData.Type): ChatEntity.Type {
    return when (from) {
      ChatData.Type.DEFAULT -> ChatEntity.Type.DEFAULT
      ChatData.Type.SYSTEM -> ChatEntity.Type.SYSTEM
    }
  }
}

internal fun ChatEntity.Type.asData(): ChatData.Type {
  return ChatTypeLocalMapper.mapToRight(this)
}

internal fun ChatData.Type.asEntity(): ChatEntity.Type {
  return ChatTypeLocalMapper.mapToLeft(this)
}