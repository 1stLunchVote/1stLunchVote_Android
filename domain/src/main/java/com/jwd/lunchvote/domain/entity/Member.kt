package com.jwd.lunchvote.domain.entity

data class Member(
    val uid: String? = null,
    val nickname: String? = null,
    val profileImage: String? = null,
    val ready: Boolean = false,
    val joinedTime: String? = null
)
