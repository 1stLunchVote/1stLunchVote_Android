package com.jwd.lunchvote.data.mapper

import com.jwd.lunchvote.core.common.mapper.Mapper
import com.jwd.lunchvote.data.mapper.type.MemberStatusDataTypeMapper
import com.jwd.lunchvote.data.model.MemberData
import com.jwd.lunchvote.domain.entity.Member

internal object MemberDataMapper : Mapper<MemberData, Member> {
    override fun mapToRight(from: MemberData): Member {
        return Member(
            id = from.id,
            loungeId = from.loungeId,
            name = from.name,
            profileImage = from.profileImage,
            status = from.status.let(MemberStatusDataTypeMapper::mapToRight),
            isOwner = from.isOwner,
            joinedAt = from.joinedAt
        )
    }
}