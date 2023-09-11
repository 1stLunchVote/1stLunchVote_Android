package com.jwd.lunchvote.local.room.mapper

import com.jwd.lunchvote.core.common.base.BiMapper
import com.jwd.lunchvote.data.model.MemberData
import com.jwd.lunchvote.local.room.entity.MemberEntity

internal object MemberEntityMapper : BiMapper<MemberEntity, MemberData> {
    override fun mapToRight(from: MemberEntity): MemberData {
        return MemberData(
            id = from.id,
            loungeId = from.loungeId,
            name = from.name,
            profileImage = from.profileImage,
            status = from.status,
            isOwner = from.isOwner,
            joinedAt = from.joinedAt
        )
    }

    override fun mapToLeft(from: MemberData): MemberEntity {
        return MemberEntity(
            id = from.id,
            loungeId = from.loungeId,
            name = from.name,
            profileImage = from.profileImage,
            status = from.status,
            isOwner = from.isOwner,
            joinedAt = from.joinedAt
        )
    }
}