package com.jwd.lunchvote.data.repository

import com.jwd.lunchvote.data.mapper.asDomain
import com.jwd.lunchvote.data.source.remote.FoodDataSource
import com.jwd.lunchvote.domain.entity.Food
import com.jwd.lunchvote.domain.repository.HomeRepository
import javax.inject.Inject

class HomeRepositoryImpl @Inject constructor(
  private val foodDataSource: FoodDataSource
) : HomeRepository {
  override suspend fun getFoodTrend(): Pair<Food, Float> {
    val (foodData, trend) = foodDataSource.getFoodTrend()
    return foodData.asDomain() to trend
  }
}