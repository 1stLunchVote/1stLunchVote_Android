package com.jwd.lunchvote.presentation.model

import android.os.Parcelable
import com.jwd.lunchvote.presentation.util.getInitialDateTime
import kotlinx.parcelize.Parcelize
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime

@Parcelize
data class UserUIModel(
  val id: String = "",
  val email: String = "",
  val name: String = "",
  val profileImage: String = "https://firebasestorage.googleapis.com/v0/b/lunch-vote-ed5de.appspot.com/o/App%20Icon.png?alt=media&token=a80fd77c-ebd9-4b16-91d4-4830a456d4f8",
  val createdAt: ZonedDateTime = getInitialDateTime(),
  val deletedAt: ZonedDateTime? = null
): Parcelable