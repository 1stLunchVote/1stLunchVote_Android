package com.jwd.lunchvote.presentation.model

import java.time.LocalDateTime

data class UserUIModel(
  val id: String = "",
  val email: String = "",
  val name: String = "",
  val profileImageUrl: String = "",
  val createdAt: String = LocalDateTime.now().toString()
)