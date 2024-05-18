package com.jwd.lunchvote.presentation.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.time.LocalDateTime

@Parcelize
data class TemplateUIModel(
  val id: String = "",
  val userId: String = "",
  val name: String = "",
  val likedFoodIds: List<String> = emptyList(),
  val dislikedFoodIds: List<String> = emptyList(),
  val createdAt: LocalDateTime = LocalDateTime.now(),
  val deletedAt: LocalDateTime? = null
): Parcelable
