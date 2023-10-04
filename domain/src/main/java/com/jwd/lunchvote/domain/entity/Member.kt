package com.jwd.lunchvote.domain.entity

import com.jwd.lunchvote.domain.entity.type.MemberStatusType

data class Member(
    val id: String,
    val loungeId: String,
    val name: String,
    val profileImage: String?,
    val status: MemberStatusType,
    val isOwner: Boolean,
    val joinedAt: String
)
