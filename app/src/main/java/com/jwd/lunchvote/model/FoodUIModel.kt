package com.jwd.lunchvote.model

import android.os.Parcelable
import com.jwd.lunchvote.domain.entity.Food
import com.jwd.lunchvote.model.enums.FoodStatus
import kotlinx.parcelize.Parcelize

@Parcelize
data class FoodUIModel (
  val id: String,
  val imageUrl: String,
  val name: String
): Parcelable {
  constructor(
    food: Food
  ): this(
    id = food.id,
    imageUrl = food.imageUrl,
    name = food.name
  )
}

fun Map<FoodUIModel, FoodStatus>.updateFoodMap(food: FoodUIModel): Map<FoodUIModel, FoodStatus> =
  this.toMutableMap().apply { this[food] = this[food]?.nextStatus() ?: FoodStatus.DEFAULT }