package com.jwd.lunchvote.mapper.lounge

import com.jwd.lunchvote.domain.entity.LoungeChat
import com.jwd.lunchvote.model.ChatUIModel

internal object ChatUIMapper{
    fun mapToRight(loungeChat: LoungeChat, isMine: Boolean = false) : ChatUIModel {
        return ChatUIModel(
            loungeChat.message,
            loungeChat.messageType,
            isMine,
            loungeChat.userId,
            loungeChat.createdAt,
            loungeChat.userProfile,
            loungeChat.sendStatus
        )
    }
}