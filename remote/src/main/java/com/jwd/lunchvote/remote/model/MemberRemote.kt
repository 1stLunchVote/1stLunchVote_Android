package com.jwd.lunchvote.remote.model

import java.time.ZonedDateTime

data class MemberRemote(
  val loungeId: String = "",
  val status: String = "",
  val joinedAt: String = ZonedDateTime.now().toString()
)