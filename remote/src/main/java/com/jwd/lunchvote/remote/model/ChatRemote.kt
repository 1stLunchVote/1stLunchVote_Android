package com.jwd.lunchvote.remote.model

import com.google.firebase.Timestamp
import com.jwd.lunchvote.remote.util.toLong

data class ChatRemote(
  val userId: String? = null,
  val message: String = "",
  val type: String = TYPE_DEFAULT,
  val createdAt: Long = Timestamp.now().toLong(),
) {

  companion object {
    const val TYPE_DEFAULT = "default"
    const val TYPE_SYSTEM = "system"
  }
}