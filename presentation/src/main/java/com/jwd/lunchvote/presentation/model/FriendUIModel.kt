package com.jwd.lunchvote.presentation.model

import android.os.Parcelable
import com.jwd.lunchvote.presentation.util.getInitialDateTime
import kotlinx.parcelize.Parcelize
import java.time.ZonedDateTime

@Parcelize
data class FriendUIModel(
  val id: String = "",
  val userId: String = "",
  val friendId: String = "",
  val createdAt: ZonedDateTime = getInitialDateTime(),
  val matchedAt: ZonedDateTime? = null,
  val deletedAt: ZonedDateTime? = null
) : Parcelable