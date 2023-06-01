package com.jwd.lunchvote.domain.entity

data class Member(
    val nickname: String? = null,
    val profileImage: String? = null,
    val isReady: Boolean = false,
    val joinedTime: String? = null
)
