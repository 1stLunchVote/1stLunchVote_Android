package com.jwd.lunchvote.presentation.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.time.LocalDateTime

@Parcelize
data class TemplateUIModel(
  val id: String = "",
  val userId: String = "",
  val name: String = "",
  val like: List<String> = emptyList(),
  val dislike: List<String> = emptyList(),
  val createdAt: String = LocalDateTime.now().toString(),
  val deletedAt: String? = null
): Parcelable
