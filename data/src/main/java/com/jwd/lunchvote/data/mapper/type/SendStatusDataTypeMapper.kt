package com.jwd.lunchvote.data.mapper.type

import com.jwd.lunchvote.core.common.base.BiMapper
import com.jwd.lunchvote.data.model.type.SendStatusDataType
import com.jwd.lunchvote.domain.entity.type.SendStatusType

internal object SendStatusDataTypeMapper : BiMapper<SendStatusDataType, SendStatusType>{
    override fun mapToRight(from: SendStatusDataType): SendStatusType {
        return when (from) {
            SendStatusDataType.SUCCESS -> SendStatusType.SUCCESS
            SendStatusDataType.SENDING -> SendStatusType.SENDING
            SendStatusDataType.FAIL -> SendStatusType.FAIL
        }
    }

    override fun mapToLeft(from: SendStatusType): SendStatusDataType {
        return when (from) {
            SendStatusType.SUCCESS -> SendStatusDataType.SUCCESS
            SendStatusType.SENDING -> SendStatusDataType.SENDING
            SendStatusType.FAIL -> SendStatusDataType.FAIL
        }
    }
}