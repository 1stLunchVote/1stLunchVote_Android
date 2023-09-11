package com.jwd.lunchvote.data.mapper.type

import com.jwd.lunchvote.core.common.base.BiMapper
import com.jwd.lunchvote.data.model.type.MemberStatusDataType
import com.jwd.lunchvote.domain.entity.type.MemberStatusType

object MemberStatusDataTypeMapper : BiMapper<MemberStatusDataType, MemberStatusType>{
    override fun mapToRight(from: MemberStatusDataType): MemberStatusType {
        return when(from){
            MemberStatusDataType.JOINED -> MemberStatusType.JOINED
            MemberStatusDataType.READY -> MemberStatusType.READY
            else -> MemberStatusType.EXILED
        }
    }

    override fun mapToLeft(from: MemberStatusType): MemberStatusDataType {
        return when(from){
            MemberStatusType.JOINED -> MemberStatusDataType.JOINED
            MemberStatusType.READY -> MemberStatusDataType.READY
            else -> MemberStatusDataType.EXILED
        }
    }
}