package com.jwd.lunchvote.domain.repository

interface StorageRepository {

  suspend fun uploadFoodImage(foodName: String, image: ByteArray): String
  suspend fun getFoodImageUri(foodName: String): String
  suspend fun uploadProfileImage(userId: String, image: ByteArray): String
}