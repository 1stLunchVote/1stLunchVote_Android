package com.jwd.lunchvote.data.mapper

import com.jwd.lunchvote.core.common.base.BiMapper
import com.jwd.lunchvote.data.model.FoodData
import com.jwd.lunchvote.domain.entity.Food

object FoodDataMapper: BiMapper<FoodData, Food> {
  override fun mapToRight(from: FoodData): Food {
    return Food(
      id = from.id,
      imageUrl = from.imageUrl,
      name = from.name
    )
  }

  override fun mapToLeft(from: Food): FoodData {
    return FoodData(
      id = from.id,
      imageUrl = from.imageUrl,
      name = from.name
    )
  }
}

internal fun Food.asData(): FoodData {
  return FoodDataMapper.mapToLeft(this)
}

internal fun FoodData.asDomain(): Food {
  return FoodDataMapper.mapToRight(this)
}