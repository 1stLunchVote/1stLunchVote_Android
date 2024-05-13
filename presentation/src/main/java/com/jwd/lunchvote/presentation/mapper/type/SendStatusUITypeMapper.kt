package com.jwd.lunchvote.presentation.mapper.type

import com.jwd.lunchvote.core.common.mapper.BiMapper
import com.jwd.lunchvote.domain.entity.type.SendStatusType
import com.jwd.lunchvote.presentation.model.type.SendStatusUIType

private object SendStatusUITypeMapper : BiMapper<SendStatusUIType, SendStatusType> {
  override fun mapToRight(from: SendStatusUIType): SendStatusType {
    return when (from) {
      SendStatusUIType.SUCCESS -> SendStatusType.SUCCESS
      SendStatusUIType.SENDING -> SendStatusType.SENDING
      SendStatusUIType.FAIL -> SendStatusType.FAIL
    }
  }

  override fun mapToLeft(from: SendStatusType): SendStatusUIType {
    return when (from) {
      SendStatusType.SUCCESS -> SendStatusUIType.SUCCESS
      SendStatusType.SENDING -> SendStatusUIType.SENDING
      SendStatusType.FAIL -> SendStatusUIType.FAIL
    }
  }
}

internal fun SendStatusUIType.asDomain(): SendStatusType {
  return SendStatusUITypeMapper.mapToRight(this)
}

internal fun SendStatusType.asUI(): SendStatusUIType {
  return SendStatusUITypeMapper.mapToLeft(this)
}