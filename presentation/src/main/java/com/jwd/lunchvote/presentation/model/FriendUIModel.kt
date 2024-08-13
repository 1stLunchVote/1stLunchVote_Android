package com.jwd.lunchvote.presentation.model

import android.os.Parcelable
import com.jwd.lunchvote.presentation.util.INITIAL_DATE_TIME
import kotlinx.parcelize.Parcelize
import java.time.ZonedDateTime

@Parcelize
data class FriendUIModel(
  val id: String = "",
  val userId: String = "",
  val friendId: String = "",
  val createdAt: ZonedDateTime = INITIAL_DATE_TIME,
  val matchedAt: ZonedDateTime? = null,
  val deletedAt: ZonedDateTime? = null
) : Parcelable