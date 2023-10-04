package com.jwd.lunchvote.data.mapper.type

import com.jwd.lunchvote.core.common.base.Mapper
import com.jwd.lunchvote.data.model.type.LoungeStatusDataType
import com.jwd.lunchvote.domain.entity.type.LoungeStatusType

internal object LoungeStatusDataTypeMapper : Mapper<LoungeStatusDataType, LoungeStatusType>{
    override fun mapToRight(from: LoungeStatusDataType): LoungeStatusType {
        return when(from){
            LoungeStatusDataType.STARTED -> LoungeStatusType.STARTED
            LoungeStatusDataType.FINISHED -> LoungeStatusType.FINISHED
            else -> LoungeStatusType.CREATED
        }
    }
}