package com.jwd.lunchvote.presentation.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.time.Instant
import java.time.ZoneId
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
  val createdAt: ZonedDateTime = ZonedDateTime.ofInstant(Instant.now(), ZoneId.of("Asia/Seoul"))
) : Parcelable {

  enum class Type {
    DEFAULT, SYSTEM
  }
}