package com.jwd.lunchvote.presentation.model

import android.os.Parcelable
import com.jwd.lunchvote.domain.entity.Template
import kotlinx.parcelize.Parcelize

@Parcelize
data class TemplateUIModel(
  val id: String = "",
  val userId: String = "",
  val name: String = "",
  val like: List<String> = emptyList(),
  val dislike: List<String> = emptyList()
): Parcelable
