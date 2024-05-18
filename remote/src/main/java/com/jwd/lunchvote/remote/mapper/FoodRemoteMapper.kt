package com.jwd.lunchvote.remote.mapper

import com.jwd.lunchvote.core.common.mapper.BiMapper
import com.jwd.lunchvote.data.model.FoodData
import com.jwd.lunchvote.remote.model.FoodRemote

internal object FoodRemoteMapper: BiMapper<FoodRemote, FoodData> {
  override fun mapToRight(from: FoodRemote): FoodData {
    return FoodData(
      id = "",
      name = from.name,
      image = from.image
    )
  }

  override fun mapToLeft(from: FoodData): FoodRemote {
    return FoodRemote(
      name = from.name,
      image = from.image
    )
  }
}

internal fun FoodData.asRemote(): FoodRemote {
  return FoodRemoteMapper.mapToLeft(this)
}

internal fun FoodRemote.asData(id: String): FoodData {
  return FoodRemoteMapper.mapToRight(this).copy(id = id)
}