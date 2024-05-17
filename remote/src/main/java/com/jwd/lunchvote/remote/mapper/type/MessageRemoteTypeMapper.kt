package com.jwd.lunchvote.remote.mapper.type

import com.jwd.lunchvote.core.common.mapper.BiMapper
import com.jwd.lunchvote.data.model.type.MessageDataType

private object MessageRemoteTypeMapper : BiMapper<Int, MessageDataType> {
  override fun mapToRight(from: Int): MessageDataType {
    return when (from) {
      0 -> MessageDataType.NORMAL
      1 -> MessageDataType.CREATE
      2 -> MessageDataType.JOIN
      3 -> MessageDataType.EXIT
      else -> MessageDataType.EXILE
    }
  }

  override fun mapToLeft(from: MessageDataType): Int {
    return when (from) {
      MessageDataType.NORMAL -> 0
      MessageDataType.CREATE -> 1
      MessageDataType.JOIN -> 2
      MessageDataType.EXIT -> 3
      MessageDataType.EXILE -> 4
    }
  }
}

internal fun Int.asMessageDataType(): MessageDataType {
  return MessageRemoteTypeMapper.mapToRight(this)
}

internal fun MessageDataType.asRemote(): Int {
  return MessageRemoteTypeMapper.mapToLeft(this)
}