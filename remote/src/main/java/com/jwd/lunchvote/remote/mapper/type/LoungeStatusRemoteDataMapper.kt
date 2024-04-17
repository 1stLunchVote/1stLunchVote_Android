package com.jwd.lunchvote.remote.mapper.type

import com.jwd.lunchvote.core.common.mapper.Mapper
import com.jwd.lunchvote.data.model.type.LoungeStatusDataType

internal object LoungeStatusRemoteDataMapper : Mapper<String?, LoungeStatusDataType> {
    override fun mapToRight(from: String?): LoungeStatusDataType {
        return when(from){
            "started" -> LoungeStatusDataType.STARTED
            "finished" -> LoungeStatusDataType.FINISHED
            else -> LoungeStatusDataType.CREATED
        }
    }
}