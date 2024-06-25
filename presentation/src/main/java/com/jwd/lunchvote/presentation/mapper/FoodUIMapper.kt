package com.jwd.lunchvote.presentation.mapper

import com.jwd.lunchvote.core.common.mapper.BiMapper
import com.jwd.lunchvote.domain.entity.Food
import com.jwd.lunchvote.presentation.model.FoodUIModel

private object FoodUIMapper : BiMapper<FoodUIModel, Food> {
  override fun mapToRight(from: FoodUIModel): Food =
    Food(
      id = from.id,
      name = from.name
    )

  override fun mapToLeft(from: Food): FoodUIModel =
    FoodUIModel(
      id = from.id,
      name = from.name
    )
}

internal fun FoodUIModel.asDomain(): Food =
  FoodUIMapper.mapToRight(this)

internal fun Food.asUI(): FoodUIModel =
  FoodUIMapper.mapToLeft(this)