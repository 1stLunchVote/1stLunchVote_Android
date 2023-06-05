package com.jwd.lunchvote.domain.entity

data class LoungeChat(
    val sender: String? = null,
    val senderProfile: String? = null,
    val content: String? = null,
    // messageType: 0 = 일반 메시지, 1 = 방 생성 및 참가
    val messageType: Int = 0,
    val createdAt: String? = null
)
