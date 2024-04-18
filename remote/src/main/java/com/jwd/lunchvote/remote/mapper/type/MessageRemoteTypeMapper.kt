package com.jwd.lunchvote.remote.mapper.type

import com.jwd.lunchvote.core.common.mapper.BiMapper
import com.jwd.lunchvote.data.model.type.MessageDataType

internal object MessageRemoteTypeMapper : BiMapper<Int, MessageDataType> {
    override fun mapToRight(from: Int): MessageDataType {
        return when (from) {
            0 -> MessageDataType.NORMAL
            1 -> MessageDataType.CREATE
            2 -> MessageDataType.JOIN
            else -> MessageDataType.EXIT
        }
    }

    override fun mapToLeft(from: MessageDataType): Int {
        return when (from) {
            MessageDataType.NORMAL -> 0
            MessageDataType.CREATE -> 1
            MessageDataType.JOIN -> 2
            MessageDataType.EXIT -> 3
        }
    }
}