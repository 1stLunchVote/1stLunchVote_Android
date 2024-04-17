package com.jwd.lunchvote.remote.mapper

import com.jwd.lunchvote.core.common.mapper.Mapper
import com.jwd.lunchvote.data.model.MemberData
import com.jwd.lunchvote.remote.mapper.type.MemberStatusRemoteTypeMapper
import com.jwd.lunchvote.remote.model.MemberRemote

internal object MemberRemoteMapper : Mapper<MemberRemote, MemberData>   {
    override fun mapToRight(from: MemberRemote): MemberData {
        return MemberData(
            id = from.id.orEmpty(),
            loungeId = from.loungeId.orEmpty(),
            name = from.name.orEmpty(),
            profileImage = from.profileImage,
            status = from.status.let(MemberStatusRemoteTypeMapper::mapToRight),
            isOwner = from.isOwner,
            joinedAt = from.joinedAt.orEmpty()
        )
    }
}