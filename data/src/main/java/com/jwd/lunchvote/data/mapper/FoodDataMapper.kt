package com.jwd.lunchvote.data.mapper

import com.jwd.lunchvote.data.model.FoodData
import com.jwd.lunchvote.domain.entity.Food
import com.jwd.lunchvote.mapper.BiMapper

private object FoodDataMapper: BiMapper<FoodData, Food> {
  override fun mapToRight(from: FoodData): Food =
    Food(
      id = from.id,
      name = from.name,
      imageUrl = from.imageUrl
    )

  override fun mapToLeft(from: Food): FoodData =
    FoodData(
      id = from.id,
      name = from.name,
      imageUrl = from.imageUrl
    )
}

internal fun FoodData.asDomain(): Food =
  FoodDataMapper.mapToRight(this)

internal fun Food.asData(): FoodData =
  FoodDataMapper.mapToLeft(this)