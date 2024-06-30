package com.jwd.lunchvote.presentation.model

import com.jwd.lunchvote.presentation.util.getInitialDateTime
import java.time.ZonedDateTime

data class FriendUIModel(
  val id: String = "",
  val userId: String = "",
  val friendId: String = "",
  val createdAt: ZonedDateTime = getInitialDateTime(),
  val matchedAt: ZonedDateTime? = null,
  val deletedAt: ZonedDateTime? = null
)