package com.jwd.lunchvote.domain.usecase

import com.jwd.lunchvote.domain.entity.Food
import com.jwd.lunchvote.domain.repository.FoodRepository
import com.jwd.lunchvote.domain.repository.StorageRepository
import javax.inject.Inject

class CreateFood @Inject constructor(
  private val foodRepository: FoodRepository,
  private val storageRepository: StorageRepository
) {

  suspend operator fun invoke(food: Food) {
    foodRepository.createFood(food)
    storageRepository.uploadFoodImage(food.name, food.image)
  }
}