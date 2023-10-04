package com.jwd.lunchvote.data.mapper.type

import com.jwd.lunchvote.core.common.base.BiMapper
import com.jwd.lunchvote.data.model.type.MessageDataType
import com.jwd.lunchvote.domain.entity.type.MessageType

internal object MessageDataTypeMapper : BiMapper<MessageDataType, MessageType> {
    override fun mapToRight(from: MessageDataType): MessageType {
        return when (from) {
            MessageDataType.NORMAL -> MessageType.NORMAL
            MessageDataType.CREATE -> MessageType.CREATE
            MessageDataType.JOIN -> MessageType.JOIN
            MessageDataType.EXIT -> MessageType.EXIT
        }
    }

    override fun mapToLeft(from: MessageType): MessageDataType {
        return when (from) {
            MessageType.NORMAL -> MessageDataType.NORMAL
            MessageType.CREATE -> MessageDataType.CREATE
            MessageType.JOIN -> MessageDataType.JOIN
            MessageType.EXIT -> MessageDataType.EXIT
        }
    }
}