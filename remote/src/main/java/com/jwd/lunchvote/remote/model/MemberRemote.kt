package com.jwd.lunchvote.remote.model

import java.time.ZonedDateTime

data class MemberRemote(
  val userName: String = "",
  val userProfile: String = "",
  val loungeId: String = "",
  val status: String = "",
  val joinedAt: String = ZonedDateTime.now().toString()
)