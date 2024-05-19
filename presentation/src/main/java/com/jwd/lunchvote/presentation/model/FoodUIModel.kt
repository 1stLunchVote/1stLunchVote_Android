package com.jwd.lunchvote.presentation.model

import android.os.Parcelable
import com.jwd.lunchvote.presentation.model.type.FoodStatusUIType
import kotlinx.parcelize.Parcelize

@Parcelize
data class FoodUIModel (
  val id: String = "",
  val name: String = "",
  val image: String = ""
): Parcelable

internal fun Map<FoodUIModel, FoodStatusUIType>.updateFoodMap(food: FoodUIModel): Map<FoodUIModel, FoodStatusUIType> =
  this.toMutableMap().apply { this[food] = this[food]?.nextStatus() ?: FoodStatusUIType.DEFAULT }