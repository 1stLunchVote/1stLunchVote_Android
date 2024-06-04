package com.jwd.lunchvote.presentation.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class LoungeUIModel(
  val id: String = "",
  val status: Status = Status.CREATED,
  val members: Int = 0
): Parcelable {

  enum class Status {
    CREATED, QUIT, FIRST_VOTE, SECOND_VOTE, FINISHED
  }
}
