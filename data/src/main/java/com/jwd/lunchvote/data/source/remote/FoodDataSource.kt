package com.jwd.lunchvote.data.source.remote

import com.jwd.lunchvote.data.model.FoodData

interface FoodDataSource {
  suspend fun getFoodList(): List<FoodData>
}