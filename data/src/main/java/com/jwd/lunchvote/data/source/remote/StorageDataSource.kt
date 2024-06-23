package com.jwd.lunchvote.data.source.remote

interface StorageDataSource {

  suspend fun uploadFoodImage(foodName: String, image: ByteArray)
  suspend fun getFoodImage(foodName: String): ByteArray
  suspend fun uploadProfileImage(userId: String, image: ByteArray): String
}