package com.jwd.lunchvote.mapper.lounge

import com.jwd.lunchvote.domain.entity.Member
import com.jwd.lunchvote.domain.entity.type.MemberStatusType
import com.jwd.lunchvote.model.MemberUIModel

internal object MemberUIMapper {
    fun mapToRight(member: Member, isMine: Boolean = false) : MemberUIModel {
        return MemberUIModel(
            member.id,
            member.name.ifEmpty { "익명" },
            member.profileImage,
            member.status == MemberStatusType.READY,
            member.isOwner,
            isMine
        )
    }
}