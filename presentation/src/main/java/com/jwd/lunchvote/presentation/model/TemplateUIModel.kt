package com.jwd.lunchvote.presentation.model

import android.os.Parcelable
import com.jwd.lunchvote.presentation.util.getInitialDateTime
import kotlinx.parcelize.Parcelize
import java.time.ZonedDateTime

@Parcelize
data class TemplateUIModel(
  val id: String = "",
  val userId: String = "",
  val name: String = "",
  val likedFoodIds: List<String> = emptyList(),
  val dislikedFoodIds: List<String> = emptyList(),
  val createdAt: ZonedDateTime = getInitialDateTime(),
  val deletedAt: ZonedDateTime? = null
): Parcelable
