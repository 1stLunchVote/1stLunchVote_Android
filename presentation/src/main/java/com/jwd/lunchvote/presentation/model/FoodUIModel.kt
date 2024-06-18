package com.jwd.lunchvote.presentation.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class FoodUIModel (
  val id: String = "",
  val name: String = "",
  val image: String = ""
): Parcelable

enum class FoodStatus {
  DEFAULT, LIKE, DISLIKE;

  fun nextStatus(): FoodStatus = when (this) {
    DEFAULT -> LIKE
    LIKE -> DISLIKE
    DISLIKE -> DEFAULT
  }
}

internal fun Map<FoodUIModel, FoodStatus>.updateFoodMap(food: FoodUIModel): Map<FoodUIModel, FoodStatus> =
  this.toMutableMap().apply { this[food] = this[food]?.nextStatus() ?: FoodStatus.DEFAULT }