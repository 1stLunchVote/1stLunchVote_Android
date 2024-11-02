package com.jwd.lunchvote.presentation.mapper

import kr.co.inbody.library.mapper.BiMapper
import com.jwd.lunchvote.domain.entity.ContactReply
import com.jwd.lunchvote.presentation.model.ContactReplyUIModel
import com.jwd.lunchvote.presentation.util.toLong
import com.jwd.lunchvote.presentation.util.toZonedDateTime

private object ContactReplyUIMapper : BiMapper<ContactReplyUIModel, ContactReply> {
  override fun mapToRight(from: ContactReplyUIModel): ContactReply =
    ContactReply(
      id = from.id,
      contactId = from.contactId,
      title = from.title,
      content = from.content,
      createdAt = from.createdAt.toLong()
    )

  override fun mapToLeft(from: ContactReply): ContactReplyUIModel =
    ContactReplyUIModel(
      id = from.id,
      contactId = from.contactId,
      title = from.title,
      content = from.content,
      createdAt = from.createdAt.toZonedDateTime()
    )
}

internal fun ContactReplyUIModel.asDomain(): ContactReply =
  ContactReplyUIMapper.mapToRight(this)

internal fun ContactReply.asUI(): ContactReplyUIModel =
  ContactReplyUIMapper.mapToLeft(this)