package com.jwd.lunchvote.data.mapper

import com.jwd.lunchvote.core.common.mapper.BiMapper
import com.jwd.lunchvote.data.mapper.type.MessageDataTypeMapper
import com.jwd.lunchvote.data.mapper.type.SendStatusDataTypeMapper
import com.jwd.lunchvote.data.model.LoungeChatData
import com.jwd.lunchvote.domain.entity.LoungeChat

internal object LoungeChatDataMapper : BiMapper<LoungeChatData, LoungeChat> {
    override fun mapToRight(from: LoungeChatData): LoungeChat {
        return LoungeChat(
            id = from.id,
            loungeId = from.loungeId,
            userId = from.userId,
            userProfile = from.userProfile,
            message = from.message,
            messageType = from.messageType.let(MessageDataTypeMapper::mapToRight),
            createdAt = from.createdAt,
            sendStatus = from.sendStatus.let(SendStatusDataTypeMapper::mapToRight)
        )
    }

    override fun mapToLeft(from: LoungeChat): LoungeChatData {
        return LoungeChatData(
            id = from.id,
            loungeId = from.loungeId,
            userId = from.userId,
            userProfile = from.userProfile,
            message = from.message,
            messageType = from.messageType.let(MessageDataTypeMapper::mapToLeft),
            createdAt = from.createdAt,
            sendStatus = from.sendStatus.let(SendStatusDataTypeMapper::mapToLeft)
        )
    }
}