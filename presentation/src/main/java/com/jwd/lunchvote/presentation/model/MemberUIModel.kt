package com.jwd.lunchvote.presentation.model

import android.os.Parcelable
import com.jwd.lunchvote.presentation.util.getInitialDateTime
import kotlinx.parcelize.Parcelize
import java.time.ZonedDateTime

@Parcelize
data class MemberUIModel(
  val loungeId: String = "",
  val userId: String = "",
  val userName: String = "",
  val userProfile: String = "",
  val type: Type = Type.DEFAULT,
  val status: Status = Status.STANDBY,
  val createdAt: ZonedDateTime = getInitialDateTime(),
  val deletedAt: ZonedDateTime? = null
) : Parcelable {

  enum class Type {
    DEFAULT, OWNER, READY, EXILED
  }

  enum class Status {
    STANDBY, VOTING, VOTED
  }
}
