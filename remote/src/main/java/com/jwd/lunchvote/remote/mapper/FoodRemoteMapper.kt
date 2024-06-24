package com.jwd.lunchvote.remote.mapper

import com.jwd.lunchvote.core.common.mapper.BiMapper
import com.jwd.lunchvote.data.model.FoodData
import com.jwd.lunchvote.remote.model.FoodRemote

private object FoodRemoteMapper: BiMapper<FoodRemote, FoodData> {
  override fun mapToRight(from: FoodRemote): FoodData =
    FoodData(
      id = "",
      name = from.name
    )

  override fun mapToLeft(from: FoodData): FoodRemote =
    FoodRemote(
      name = from.name
    )
}

internal fun FoodData.asRemote(): FoodRemote =
  FoodRemoteMapper.mapToLeft(this)

internal fun FoodRemote.asData(id: String): FoodData =
  FoodRemoteMapper.mapToRight(this).copy(id = id)