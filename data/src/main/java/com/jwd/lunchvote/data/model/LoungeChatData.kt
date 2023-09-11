package com.jwd.lunchvote.data.model

import com.jwd.lunchvote.data.model.type.MessageDataType
import com.jwd.lunchvote.data.model.type.SendStatusDataType

data class LoungeChatData(
    val id: String,
    val loungeId: String,
    val userId: String,
    val userProfile: String?,
    val message: String,
    // messageType: 0 = 일반 메시지, 1 = 방 생성 및 참가
    val messageType: MessageDataType,
    val sendStatus: SendStatusDataType,
    val createdAt: String
)