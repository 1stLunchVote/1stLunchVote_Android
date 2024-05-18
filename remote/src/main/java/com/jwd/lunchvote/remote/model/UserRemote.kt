package com.jwd.lunchvote.remote.model

import com.google.firebase.Timestamp

data class UserRemote(
  val email: String = "",
  val name: String = "",
  val profileImage: String = "",
  val createdAt: Timestamp = Timestamp.now(),
  val deletedAt: Timestamp? = null
)
