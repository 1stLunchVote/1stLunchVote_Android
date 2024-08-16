package com.jwd.lunchvote.remote.model

import com.google.firebase.Timestamp

data class ContactRemote(
  val userId: String = "",
  val title: String = "",
  val category: String = "",
  val content: String = "",
  val createdAt: Timestamp = Timestamp.now(),
  val deletedAt: Timestamp? = null
) {

  companion object {
    const val CATEGORY_ACCOUNT = "account"
    const val CATEGORY_BUG = "bug"
    const val CATEGORY_SUGGESTION = "suggestion"
    const val CATEGORY_ETC = "etc"
  }
}