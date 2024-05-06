package com.jwd.lunchvote.remote.model

import java.time.ZonedDateTime

data class LoungeChatRemote(
  val loungeId: String = "",
  val userId: String = "",
  val userProfile: String = "",
  val message: String = "",
  val messageType: Int = 0,
  val createdAt: String = ZonedDateTime.now().toString(),
)