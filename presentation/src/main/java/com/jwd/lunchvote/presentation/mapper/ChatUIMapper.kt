package com.jwd.lunchvote.presentation.mapper

import com.jwd.lunchvote.core.common.mapper.BiMapper
import com.jwd.lunchvote.domain.entity.Chat
import com.jwd.lunchvote.presentation.model.ChatUIModel
import com.jwd.lunchvote.presentation.util.toLocalDateTime
import com.jwd.lunchvote.presentation.util.toLong

private object ChatUIMapper: BiMapper<ChatUIModel, Chat> {
  override fun mapToRight(from: ChatUIModel): Chat =
    Chat(
      loungeId = from.loungeId,
      id = from.id,
      userId = from.userId,
      userName = from.userName,
      userProfile = from.userProfile,
      message = from.message,
      type = from.type.asDomain(),
      createdAt = from.createdAt.toLong()
    )

  override fun mapToLeft(from: Chat): ChatUIModel =
    ChatUIModel(
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

private object ChatUITypeMapper : BiMapper<ChatUIModel.Type, Chat.Type> {
  override fun mapToRight(from: ChatUIModel.Type): Chat.Type =
    when (from) {
      ChatUIModel.Type.DEFAULT -> Chat.Type.DEFAULT
      ChatUIModel.Type.SYSTEM -> Chat.Type.SYSTEM
    }

  override fun mapToLeft(from: Chat.Type): ChatUIModel.Type =
    when (from) {
      Chat.Type.DEFAULT -> ChatUIModel.Type.DEFAULT
      Chat.Type.SYSTEM -> ChatUIModel.Type.SYSTEM
    }
}

internal fun ChatUIModel.asDomain(): Chat =
  ChatUIMapper.mapToRight(this)

internal fun Chat.asUI(): ChatUIModel =
  ChatUIMapper.mapToLeft(this)

internal fun ChatUIModel.Type.asDomain(): Chat.Type =
  ChatUITypeMapper.mapToRight(this)

internal fun Chat.Type.asUI(): ChatUIModel.Type =
  ChatUITypeMapper.mapToLeft(this)