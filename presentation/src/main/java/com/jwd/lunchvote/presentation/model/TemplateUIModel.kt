package com.jwd.lunchvote.presentation.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.ZonedDateTime

@Parcelize
data class TemplateUIModel(
  val id: String = "",
  val userId: String = "",
  val name: String = "",
  val likedFoodIds: List<String> = emptyList(),
  val dislikedFoodIds: List<String> = emptyList(),
  val createdAt: ZonedDateTime = ZonedDateTime.ofInstant(Instant.now(), ZoneId.of("Asia/Seoul")),
  val deletedAt: ZonedDateTime? = null
): Parcelable
