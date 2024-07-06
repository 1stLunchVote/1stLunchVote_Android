package com.jwd.lunchvote.presentation.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.time.ZonedDateTime

@Parcelize
data class UserStatusUIModel(
  val userId: String = "",
  val lastOnline: ZonedDateTime? = null,
  val loungeId: String? = null
) : Parcelable
