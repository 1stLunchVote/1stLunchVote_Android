package com.jwd.lunchvote.presentation.model

import java.time.LocalDateTime

data class UserUIModel(
  val id: String = "",
  val email: String = "",
  val name: String = "",
  val profileImageUrl: String = "https://firebasestorage.googleapis.com/v0/b/lunch-vote-ed5de.appspot.com/o/App%20Icon.png?alt=media&token=a80fd77c-ebd9-4b16-91d4-4830a456d4f8",
  val createdAt: String = LocalDateTime.now().toString()
)