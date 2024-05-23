package com.jwd.lunchvote.remote.mapper

import com.jwd.lunchvote.core.common.error.LoungeError
import com.jwd.lunchvote.core.common.mapper.BiMapper
import com.jwd.lunchvote.data.model.ChatData
import com.jwd.lunchvote.remote.model.ChatRemote

private object ChatRemoteMapper : BiMapper<ChatRemote, ChatData> {
  override fun mapToRight(from: ChatRemote): ChatData =
    ChatData(
      loungeId = from.loungeId,
      id = "",
      userId = from.userId,
      userName = from.userName,
      userProfile = from.userProfile,
      message = from.message,
      type = from.type.asChatDataType(),
      createdAt = from.createdAt
    )

  override fun mapToLeft(from: ChatData): ChatRemote =
    ChatRemote(
      loungeId = from.loungeId,
      userId = from.userId,
      userName = from.userName,
      userProfile = from.userProfile,
      message = from.message,
      type = from.type.asRemote(),
      createdAt = from.createdAt
    )
}

private object ChatRemoteTypeMapper : BiMapper<String, ChatData.Type> {
  override fun mapToRight(from: String): ChatData.Type =
    when (from) {
      ChatRemote.TYPE_DEFAULT -> ChatData.Type.DEFAULT
      ChatRemote.TYPE_SYSTEM -> ChatData.Type.SYSTEM
      else -> throw LoungeError.InvalidChatType
    }

  override fun mapToLeft(from: ChatData.Type): String =
    when (from) {
      ChatData.Type.DEFAULT -> ChatRemote.TYPE_DEFAULT
      ChatData.Type.SYSTEM -> ChatRemote.TYPE_SYSTEM
    }
}

internal fun ChatRemote.asData(id: String): ChatData =
  ChatRemoteMapper.mapToRight(this).copy(id = id)

internal fun ChatData.asRemote(): ChatRemote =
  ChatRemoteMapper.mapToLeft(this)

internal fun String.asChatDataType(): ChatData.Type =
  ChatRemoteTypeMapper.mapToRight(this)

internal fun ChatData.Type.asRemote(): String =
  ChatRemoteTypeMapper.mapToLeft(this)