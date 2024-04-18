package com.jwd.lunchvote.local.room.mapper

import com.jwd.lunchvote.core.common.mapper.BiMapper
import com.jwd.lunchvote.data.model.LoungeChatData
import com.jwd.lunchvote.local.room.entity.ChatEntity

internal object ChatEntityMapper : BiMapper<ChatEntity, LoungeChatData> {
    override fun mapToRight(from: ChatEntity): LoungeChatData {
        return LoungeChatData(
            id = from.id,
            userId = from.userId,
            loungeId = from.loungeId,
            userProfile = from.userProfile,
            message = from.message,
            messageType = from.messageType,
            sendStatus = from.sendStatus,
            createdAt = from.createdAt
        )
    }

    override fun mapToLeft(from: LoungeChatData): ChatEntity {
        return ChatEntity(
            id = from.id,
            userId = from.userId,
            loungeId = from.loungeId,
            userProfile = from.userProfile,
            message = from.message,
            messageType = from.messageType,
            sendStatus = from.sendStatus,
            createdAt = from.createdAt
        )
    }
}