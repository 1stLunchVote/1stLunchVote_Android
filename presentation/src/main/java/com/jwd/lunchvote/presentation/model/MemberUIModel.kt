package com.jwd.lunchvote.presentation.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime

@Parcelize
data class MemberUIModel(
  val loungeId: String = "",
  val userId: String = "",
  val userName: String = "",
  val userProfile: String = "",
  val type: Type = Type.DEFAULT,
  val createdAt: ZonedDateTime = ZonedDateTime.ofInstant(Instant.now(), ZoneId.of("Asia/Seoul")),
  val deletedAt: ZonedDateTime? = null
) : Parcelable {

  enum class Type {
    DEFAULT, OWNER, READY, EXILED
  }
}
