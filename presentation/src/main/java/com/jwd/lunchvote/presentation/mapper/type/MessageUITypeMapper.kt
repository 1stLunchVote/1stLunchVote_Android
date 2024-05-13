package com.jwd.lunchvote.presentation.mapper.type

import com.jwd.lunchvote.core.common.mapper.BiMapper
import com.jwd.lunchvote.domain.entity.type.MessageType
import com.jwd.lunchvote.presentation.model.type.MessageUIType

private object MessageUITypeMapper : BiMapper<MessageUIType, MessageType> {
  override fun mapToRight(from: MessageUIType): MessageType {
    return when (from) {
      MessageUIType.NORMAL -> MessageType.NORMAL
      MessageUIType.CREATE -> MessageType.CREATE
      MessageUIType.JOIN -> MessageType.JOIN
      MessageUIType.EXIT -> MessageType.EXIT
    }
  }

  override fun mapToLeft(from: MessageType): MessageUIType {
    return when (from) {
      MessageType.NORMAL -> MessageUIType.NORMAL
      MessageType.CREATE -> MessageUIType.CREATE
      MessageType.JOIN -> MessageUIType.JOIN
      MessageType.EXIT -> MessageUIType.EXIT
    }
  }
}

internal fun MessageUIType.asDomain(): MessageType {
  return MessageUITypeMapper.mapToRight(this)
}

internal fun MessageType.asUI(): MessageUIType {
  return MessageUITypeMapper.mapToLeft(this)
}