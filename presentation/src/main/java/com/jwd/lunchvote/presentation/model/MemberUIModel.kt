package com.jwd.lunchvote.presentation.model

import android.os.Parcelable
import com.jwd.lunchvote.presentation.util.INITIAL_DATE_TIME
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
  val createdAt: ZonedDateTime = INITIAL_DATE_TIME,
  val deletedAt: ZonedDateTime? = null
) : Parcelable {

  enum class Type {
    DEFAULT, OWNER, READY, LEAVED, EXILED
  }

  enum class Status {
    STANDBY, VOTING, VOTED
  }
}
