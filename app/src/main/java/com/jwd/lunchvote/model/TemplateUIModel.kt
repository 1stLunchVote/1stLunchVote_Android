package com.jwd.lunchvote.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class TemplateUIModel(
  val uid: String,
  val userId: String,
  val name: String,
  val like: List<FoodUIModel>,
  val dislike: List<FoodUIModel>
): Parcelable
