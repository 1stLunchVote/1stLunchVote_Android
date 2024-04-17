package com.jwd.lunchvote.domain.repository

import com.jwd.lunchvote.domain.entity.Food

interface HomeRepository {
  suspend fun getFoodTrend(): Pair<Food, Float>
}