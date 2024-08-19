package com.jwd.lunchvote.presentation.model

import android.os.Parcelable
import com.jwd.lunchvote.presentation.util.INITIAL_DATE_TIME
import kotlinx.parcelize.Parcelize
import java.time.ZonedDateTime

@Parcelize
data class ContactReplyUIModel(
  val id: String = "",
  val contactId: String = "",
  val title: String = "",
  val content: String = "",
  val createdAt: ZonedDateTime = INITIAL_DATE_TIME
) : Parcelable
