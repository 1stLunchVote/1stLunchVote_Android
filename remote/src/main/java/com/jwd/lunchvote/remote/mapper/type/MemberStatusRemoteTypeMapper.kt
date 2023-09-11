package com.jwd.lunchvote.remote.mapper.type

import com.jwd.lunchvote.core.common.base.BiMapper
import com.jwd.lunchvote.data.model.type.MemberStatusDataType

object MemberStatusRemoteTypeMapper : BiMapper<String?, MemberStatusDataType> {
    override fun mapToRight(from: String?): MemberStatusDataType {
        return when(from){
            "joined" -> MemberStatusDataType.JOINED
            "ready" -> MemberStatusDataType.READY
            else -> MemberStatusDataType.EXILED
        }
    }

    override fun mapToLeft(from: MemberStatusDataType): String {
        return when(from){
            MemberStatusDataType.JOINED -> "joined"
            MemberStatusDataType.READY -> "ready"
            else -> "exiled"
        }
    }
}