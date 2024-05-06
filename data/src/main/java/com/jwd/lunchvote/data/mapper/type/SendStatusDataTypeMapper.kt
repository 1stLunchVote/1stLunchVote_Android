package com.jwd.lunchvote.data.mapper.type

import com.jwd.lunchvote.core.common.mapper.BiMapper
import com.jwd.lunchvote.data.model.type.SendStatusDataType
import com.jwd.lunchvote.domain.entity.type.SendStatusType

private object SendStatusDataTypeMapper : BiMapper<SendStatusDataType, SendStatusType> {
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

internal fun SendStatusDataType.asDomain(): SendStatusType {
  return SendStatusDataTypeMapper.mapToRight(this)
}

internal fun SendStatusType.asData(): SendStatusDataType {
  return SendStatusDataTypeMapper.mapToLeft(this)
}