package com.jwd.lunchvote.remote.model

import com.google.firebase.Timestamp
import com.jwd.lunchvote.remote.util.toLong

data class MemberRemote(
  val loungeId: String = "",
  val userName: String = "",
  val userProfile: String = "",
  val type: String = TYPE_DEFAULT,
  val status: String = STATUS_STANDBY,
  val createdAt: Long = Timestamp.now().toLong(),
  val deletedAt: Long? = null
) {

  companion object {
    const val TYPE_DEFAULT = "default"
    const val TYPE_OWNER = "owner"
    const val TYPE_READY = "ready"
    const val TYPE_EXILED = "exiled"

    const val STATUS_STANDBY = "standby"
    const val STATUS_VOTING = "voting"
    const val STATUS_VOTED = "voted"
  }
}