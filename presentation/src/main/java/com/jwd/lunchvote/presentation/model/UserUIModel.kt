package com.jwd.lunchvote.presentation.model

import android.os.Parcelable
import com.jwd.lunchvote.presentation.util.INITIAL_DATE_TIME
import kotlinx.parcelize.Parcelize
import java.time.ZonedDateTime

@Parcelize
data class UserUIModel(
  val id: String = "",
  val email: String = "",
  val name: String = "",
  val profileImage: String = "",
  val createdAt: ZonedDateTime = INITIAL_DATE_TIME,
  val deletedAt: ZonedDateTime? = null
): Parcelable