package com.jwd.lunchvote.remote.model

import com.google.firebase.Timestamp

data class ContactReplyRemote(
  val contactId: String = "",
  val title: String = "",
  val content: String = "",
  val createdAt: Timestamp = Timestamp.now()
)
