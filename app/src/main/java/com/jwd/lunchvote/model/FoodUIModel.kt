package com.jwd.lunchvote.model

import android.os.Parcelable
import com.jwd.lunchvote.domain.entity.Food
import com.jwd.lunchvote.domain.entity.FoodStatus
import kotlinx.parcelize.Parcelize

@Parcelize
data class FoodUIModel (
  val id: String,
  val imageUrl: String,
  val name: String,
  val status: FoodStatus = FoodStatus.DEFAULT
): Parcelable {
  constructor(
    food: Food,
    status: FoodStatus = FoodStatus.DEFAULT
  ): this(
    id = food.id,
    imageUrl = food.imageUrl,
    name = food.name,
    status = status
  )
}