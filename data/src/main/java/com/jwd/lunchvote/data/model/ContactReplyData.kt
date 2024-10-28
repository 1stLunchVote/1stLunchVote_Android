package com.jwd.lunchvote.data.model

data class ContactReplyData(
  val id: String,
  val contactId: String,
  val title: String,
  val content: String,
  val createdAt: Long
)
