package com.jwd.lunchvote.remote.model

import com.google.firebase.Timestamp

data class FriendRemote(
  val id: String = "",
  val userId: String = "",
  val friendId: String = "",
  val createdAt: Timestamp = Timestamp.now(),
  val deletedAt: Timestamp? = null
)