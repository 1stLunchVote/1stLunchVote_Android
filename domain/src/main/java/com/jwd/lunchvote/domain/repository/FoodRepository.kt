package com.jwd.lunchvote.domain.repository

import com.jwd.lunchvote.domain.entity.Food

interface FoodRepository {

  suspend fun getAllFood(): List<Food>
  suspend fun getFoodById(id: String): Food
  suspend fun getFoodTrend(): Pair<Food, Float>
}