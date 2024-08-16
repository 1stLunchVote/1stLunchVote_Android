package com.jwd.lunchvote.domain.entity

data class ContactReply(
  val id: String,
  val contactId: String,
  val title: String,
  val content: String,
  val createdAt: Long
)
