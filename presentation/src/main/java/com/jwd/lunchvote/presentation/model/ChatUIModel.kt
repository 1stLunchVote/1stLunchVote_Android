package com.jwd.lunchvote.presentation.model

import android.os.Parcelable
import com.jwd.lunchvote.presentation.util.INITIAL_DATE_TIME
import kotlinx.parcelize.Parcelize
import java.time.ZonedDateTime

@Parcelize
data class ChatUIModel(
  val loungeId: String = "",
  val id: String = "",
  val userId: String? = null,
  val message: String = "",
  val type: Type = Type.DEFAULT,
  val createdAt: ZonedDateTime = INITIAL_DATE_TIME
) : Parcelable {

  enum class Type {
    DEFAULT, SYSTEM
  }
}