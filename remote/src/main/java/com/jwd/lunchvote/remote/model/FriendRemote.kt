package com.jwd.lunchvote.remote.model

import com.google.firebase.Timestamp

data class FriendRemote(
  val userId: String = "",
  val friendId: String = "",
  val createdAt: Timestamp = Timestamp.now(),
  val matchedAt: Timestamp? = null,
  val deletedAt: Timestamp? = null
)