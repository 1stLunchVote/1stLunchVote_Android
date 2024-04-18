package com.jwd.lunchvote.presentation.model

import android.os.Parcelable
import com.jwd.lunchvote.domain.entity.Food
import com.jwd.lunchvote.presentation.model.enums.FoodStatus
import kotlinx.parcelize.Parcelize

@Parcelize
data class FoodUIModel (
  val id: String = "",
  val imageUrl: String = "",
  val name: String = ""
): Parcelable

fun Map<FoodUIModel, FoodStatus>.updateFoodMap(food: FoodUIModel): Map<FoodUIModel, FoodStatus> =
  this.toMutableMap().apply { this[food] = this[food]?.nextStatus() ?: FoodStatus.DEFAULT }