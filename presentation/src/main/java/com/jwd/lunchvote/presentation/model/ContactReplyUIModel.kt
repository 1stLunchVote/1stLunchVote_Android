package com.jwd.lunchvote.presentation.model

import com.jwd.lunchvote.presentation.util.INITIAL_DATE_TIME
import java.time.ZonedDateTime

data class ContactReplyUIModel(
  val id: String = "",
  val contactId: String = "",
  val title: String = "",
  val content: String = "",
  val createdAt: ZonedDateTime = INITIAL_DATE_TIME
)
