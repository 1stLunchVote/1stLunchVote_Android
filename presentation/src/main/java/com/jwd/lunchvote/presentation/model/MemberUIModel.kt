package com.jwd.lunchvote.presentation.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.time.LocalDateTime

@Parcelize
data class MemberUIModel(
  val loungeId: String = "",
  val userId: String = "",
  val userName: String = "",
  val userProfile: String = "",
  val type: Type = Type.DEFAULT,
  val createdAt: LocalDateTime = LocalDateTime.now(),
  val deletedAt: LocalDateTime? = null
) : Parcelable {

  enum class Type {
    DEFAULT, OWNER, READY, EXILED
  }
}
