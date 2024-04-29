package com.jwd.lunchvote.remote.model

import java.time.LocalDateTime

data class UserRemote(
  val id: String = "",
  val email: String = "",
  val name: String = "",
  val profileImageUrl: String = "",
  val createdAt: String = LocalDateTime.now().toString()
)
