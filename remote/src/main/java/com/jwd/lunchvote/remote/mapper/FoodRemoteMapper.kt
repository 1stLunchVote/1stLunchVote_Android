package com.jwd.lunchvote.remote.mapper

import com.jwd.lunchvote.core.common.mapper.BiMapper
import com.jwd.lunchvote.data.model.FoodData
import com.jwd.lunchvote.remote.model.FoodRemote

internal object FoodRemoteMapper: BiMapper<FoodRemote, FoodData> {
  override fun mapToRight(from: FoodRemote): FoodData {
    return FoodData(
      id = from.id,
      imageUrl = from.imageUrl,
      name = from.name
    )
  }

  override fun mapToLeft(from: FoodData): FoodRemote {
    return FoodRemote(
      id = from.id,
      imageUrl = from.imageUrl,
      name = from.name
    )
  }
}

internal fun FoodData.asRemote(): FoodRemote {
  return FoodRemoteMapper.mapToLeft(this)
}

internal fun FoodRemote.asData(): FoodData {
  return FoodRemoteMapper.mapToRight(this)
}