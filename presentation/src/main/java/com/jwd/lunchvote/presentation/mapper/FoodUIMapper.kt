package com.jwd.lunchvote.presentation.mapper

import com.jwd.lunchvote.core.common.mapper.BiMapper
import com.jwd.lunchvote.domain.entity.Food
import com.jwd.lunchvote.presentation.model.FoodUIModel

internal object FoodUIMapper : BiMapper<FoodUIModel, Food> {
  override fun mapToRight(from: FoodUIModel): Food {
    return Food(
      id = from.id,
      imageUrl = from.imageUrl,
      name = from.name
    )
  }

  override fun mapToLeft(from: Food): FoodUIModel {
    return FoodUIModel(
      id = from.id,
      imageUrl = from.imageUrl,
      name = from.name
    )
  }
}

internal fun Food.asUI(): FoodUIModel {
  return FoodUIMapper.mapToLeft(this)
}

internal fun FoodUIModel.asDomain(): Food {
  return FoodUIMapper.mapToRight(this)
}