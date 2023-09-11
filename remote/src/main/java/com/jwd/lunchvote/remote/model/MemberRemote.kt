package com.jwd.lunchvote.remote.model

data class MemberRemote(
    val id: String? = null,
    val loungeId: String? = null,
    val name: String? = null,
    val profileImage: String? = null,
    val status: String? = null,
    val isOwner: Boolean = false,
    val joinedAt: String? = null
)