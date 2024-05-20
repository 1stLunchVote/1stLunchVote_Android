package com.jwd.lunchvote.data.mapper

import com.jwd.lunchvote.core.common.mapper.BiMapper
import com.jwd.lunchvote.data.model.FoodData
import com.jwd.lunchvote.domain.entity.Food

private object FoodDataMapper: BiMapper<FoodData, Food> {
  override fun mapToRight(from: FoodData): Food =
    Food(
      id = from.id,
      name = from.name,
      image = from.image
    )

  override fun mapToLeft(from: Food): FoodData =
    FoodData(
      id = from.id,
      name = from.name,
      image = from.image
    )
}

internal fun FoodData.asDomain(): Food {
  return FoodDataMapper.mapToRight(this)
}

internal fun Food.asData(): FoodData {
  return FoodDataMapper.mapToLeft(this)
}