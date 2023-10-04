package com.jwd.lunchvote.remote.model

data class LoungeChatRemote(
    val id: String? = null,
    val loungeId: String? = null,
    val userId: String? = null,
    val userProfile: String? = null,
    val message: String? = null,
    // messageType: 0 = 일반 메시지, 1 = 방 생성 및 참가
    val type: Int = 0,
    val createdAt: String? = null
)