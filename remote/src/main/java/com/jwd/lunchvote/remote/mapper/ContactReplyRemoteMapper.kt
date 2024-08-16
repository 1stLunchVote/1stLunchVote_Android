package com.jwd.lunchvote.remote.mapper

import com.jwd.lunchvote.core.common.mapper.BiMapper
import com.jwd.lunchvote.data.model.ContactReplyData
import com.jwd.lunchvote.remote.model.ContactReplyRemote
import com.jwd.lunchvote.remote.util.toLong
import com.jwd.lunchvote.remote.util.toTimestamp

private object ContactReplyRemoteMapper : BiMapper<ContactReplyRemote, ContactReplyData> {
  override fun mapToRight(from: ContactReplyRemote): ContactReplyData =
    ContactReplyData(
      id = from.id,
      contactId = from.contactId,
      title = from.title,
      content = from.content,
      createdAt = from.createdAt.toLong()
    )

  override fun mapToLeft(from: ContactReplyData): ContactReplyRemote =
    ContactReplyRemote(
      id = from.id,
      contactId = from.contactId,
      title = from.title,
      content = from.content,
      createdAt = from.createdAt.toTimestamp()
    )
}

internal fun ContactReplyRemote.asData(): ContactReplyData =
  ContactReplyRemoteMapper.mapToRight(this)

internal fun ContactReplyData.asRemote(): ContactReplyRemote =
  ContactReplyRemoteMapper.mapToLeft(this)