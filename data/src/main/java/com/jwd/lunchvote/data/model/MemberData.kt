package com.jwd.lunchvote.data.model

import com.jwd.lunchvote.data.model.type.MemberStatusDataType

data class MemberData(
    val id: String,
    val loungeId: String,
    val name: String,
    val profileImage: String?,
    val status: MemberStatusDataType,
    val isOwner: Boolean,
    val joinedAt: String
)