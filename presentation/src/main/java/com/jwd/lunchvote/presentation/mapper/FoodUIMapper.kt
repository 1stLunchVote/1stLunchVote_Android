package com.jwd.lunchvote.presentation.mapper

import com.jwd.lunchvote.core.common.mapper.BiMapper
import com.jwd.lunchvote.domain.entity.Food
import com.jwd.lunchvote.presentation.model.FoodUIModel

internal object FoodUIMapper : BiMapper<FoodUIModel, Food> {
  override fun mapToRight(from: FoodUIModel): Food {
    return Food(
      id = from.id,
      name = from.name,
      image = from.image
    )
  }

  override fun mapToLeft(from: Food): FoodUIModel {
    return FoodUIModel(
      id = from.id,
      name = from.name,
      image = from.image
    )
  }
}

internal fun Food.asUI(): FoodUIModel {
  return FoodUIMapper.mapToLeft(this)
}

internal fun FoodUIModel.asDomain(): Food {
  return FoodUIMapper.mapToRight(this)
}