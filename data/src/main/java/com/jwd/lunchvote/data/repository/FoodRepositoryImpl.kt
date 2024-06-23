package com.jwd.lunchvote.data.repository

import com.jwd.lunchvote.data.mapper.asDomain
import com.jwd.lunchvote.data.source.remote.FoodDataSource
import com.jwd.lunchvote.domain.entity.Food
import com.jwd.lunchvote.domain.repository.FoodRepository
import javax.inject.Inject

class FoodRepositoryImpl @Inject constructor(
  private val foodDataSource: FoodDataSource
): FoodRepository {

  override suspend fun getAllFood(): List<Food> =
    foodDataSource.getAllFood().map { it.asDomain() }

  override suspend fun getFoodById(id: String): Food =
    foodDataSource.getFoodById(id).asDomain()
}