package com.jwd.lunchvote.presentation.model

import android.os.Parcelable
import com.jwd.lunchvote.presentation.util.getInitialDateTime
import kotlinx.parcelize.Parcelize
import java.time.ZonedDateTime

@Parcelize
data class ChatUIModel(
  val loungeId: String = "",
  val id: String = "",
  val userId: String = "",
  val userName: String = "",
  val userProfile: String = "",
  val message: String = "",
  val type: Type = Type.DEFAULT,
  val createdAt: ZonedDateTime = getInitialDateTime()
) : Parcelable {

  enum class Type {
    DEFAULT, SYSTEM
  }
}