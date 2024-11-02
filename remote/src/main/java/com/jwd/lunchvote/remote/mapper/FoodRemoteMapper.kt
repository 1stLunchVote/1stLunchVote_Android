package com.jwd.lunchvote.remote.mapper

import com.jwd.lunchvote.mapper.BiMapper
import com.jwd.lunchvote.data.model.FoodData
import com.jwd.lunchvote.remote.model.FoodRemote

private object FoodRemoteMapper: BiMapper<FoodRemote, FoodData> {
  override fun mapToRight(from: FoodRemote): FoodData =
    FoodData(
      id = "",
      name = from.name,
      imageUrl = from.imageUrl
    )

  override fun mapToLeft(from: FoodData): FoodRemote =
    FoodRemote(
      name = from.name,
      imageUrl = from.imageUrl
    )
}

internal fun FoodData.asRemote(): FoodRemote =
  FoodRemoteMapper.mapToLeft(this)

internal fun FoodRemote.asData(id: String): FoodData =
  FoodRemoteMapper.mapToRight(this).copy(id = id)