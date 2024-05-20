package com.jwd.lunchvote.remote.mapper

import com.jwd.lunchvote.core.common.mapper.BiMapper
import com.jwd.lunchvote.data.model.ChatData
import com.jwd.lunchvote.remote.model.ChatRemote

private object ChatRemoteMapper : BiMapper<ChatRemote, ChatData> {
  override fun mapToRight(from: ChatRemote): ChatData {
    return ChatData(
      loungeId = from.loungeId,
      id = "",
      userId = from.userId,
      userName = from.userName,
      userProfile = from.userProfile,
      message = from.message,
      type = from.type.asChatTypeData(),
      createdAt = from.createdAt
    )
  }

  override fun mapToLeft(from: ChatData): ChatRemote {
    return ChatRemote(
      loungeId = from.loungeId,
      userId = from.userId,
      userName = from.userName,
      userProfile = from.userProfile,
      message = from.message,
      type = from.type.asRemote(),
      createdAt = from.createdAt
    )
  }
}

internal fun ChatRemote.asData(id: String): ChatData {
  return ChatRemoteMapper.mapToRight(this).copy(id = id)
}

internal fun ChatData.asRemote(): ChatRemote {
  return ChatRemoteMapper.mapToLeft(this)
}

private object ChatTypeRemoteMapper : BiMapper<String, ChatData.Type> {
  override fun mapToRight(from: String): ChatData.Type {
    return when (from) {
      ChatRemote.TYPE_DEFAULT -> ChatData.Type.DEFAULT
      ChatRemote.TYPE_SYSTEM -> ChatData.Type.SYSTEM
      else -> throw IllegalArgumentException("Invalid chat type")
    }
  }

  override fun mapToLeft(from: ChatData.Type): String {
    return when (from) {
      ChatData.Type.DEFAULT -> ChatRemote.TYPE_DEFAULT
      ChatData.Type.SYSTEM -> ChatRemote.TYPE_SYSTEM
    }
  }
}

internal fun ChatData.Type.asRemote(): String {
  return ChatTypeRemoteMapper.mapToLeft(this)
}

internal fun String.asChatTypeData(): ChatData.Type {
  return ChatTypeRemoteMapper.mapToRight(this)
}