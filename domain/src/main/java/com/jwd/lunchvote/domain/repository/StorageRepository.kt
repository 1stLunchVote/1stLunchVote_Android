package com.jwd.lunchvote.domain.repository

interface StorageRepository {

  suspend fun uploadFoodImage(foodName: String, image: ByteArray)
  suspend fun getFoodImage(foodName: String): ByteArray
  suspend fun uploadProfileImage(userId: String, image: ByteArray): String
}