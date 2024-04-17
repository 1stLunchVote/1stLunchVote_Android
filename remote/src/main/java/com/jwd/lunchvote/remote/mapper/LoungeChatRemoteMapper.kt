package com.jwd.lunchvote.remote.mapper

import com.jwd.lunchvote.core.common.mapper.Mapper
import com.jwd.lunchvote.data.model.LoungeChatData
import com.jwd.lunchvote.data.model.type.SendStatusDataType
import com.jwd.lunchvote.remote.mapper.type.MessageRemoteTypeMapper
import com.jwd.lunchvote.remote.model.LoungeChatRemote

internal object LoungeChatRemoteMapper : Mapper<LoungeChatRemote, LoungeChatData> {
    override fun mapToRight(from: LoungeChatRemote): LoungeChatData {
        return LoungeChatData(
            id = from.id.orEmpty(),
            loungeId = from.loungeId.orEmpty(),
            userId = from.userId.orEmpty(),
            userProfile = from.userProfile,
            message = from.message.orEmpty(),
            messageType = from.type.let(MessageRemoteTypeMapper::mapToRight),
            createdAt = from.createdAt.orEmpty(),
            sendStatus = SendStatusDataType.SUCCESS
        )
    }
}