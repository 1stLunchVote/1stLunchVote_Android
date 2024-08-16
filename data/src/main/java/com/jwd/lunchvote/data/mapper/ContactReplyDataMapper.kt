package com.jwd.lunchvote.data.mapper

import com.jwd.lunchvote.core.common.mapper.BiMapper
import com.jwd.lunchvote.data.model.ContactReplyData
import com.jwd.lunchvote.domain.entity.ContactReply

private object ContactReplyDataMapper : BiMapper<ContactReplyData, ContactReply> {
  override fun mapToRight(from: ContactReplyData): ContactReply =
    ContactReply(
      id = from.id,
      contactId = from.contactId,
      title = from.title,
      content = from.content,
      createdAt = from.createdAt
    )

  override fun mapToLeft(from: ContactReply): ContactReplyData =
    ContactReplyData(
      id = from.id,
      contactId = from.contactId,
      title = from.title,
      content = from.content,
      createdAt = from.createdAt
    )
}

internal fun ContactReplyData.asDomain(): ContactReply =
  ContactReplyDataMapper.mapToRight(this)

internal fun ContactReply.asData(): ContactReplyData =
  ContactReplyDataMapper.mapToLeft(this)