package com.jwd.lunchvote.domain.entity

data class LoungeChat(
    val chatId: Long = 0,
    val sender: String? = null,
    val senderProfile: String? = null,
    val content: String? = null,
    // messageType: 0 = 일반 메시지, 1 = 방 생성 및 참가
    val messageType: Int = 0,
    val createdAt: String? = null,
    // sendStatus: 0 = 전송완료, 1 = 전송중, 2 = 전송실패
    val sendStatus: Int = 0
)
