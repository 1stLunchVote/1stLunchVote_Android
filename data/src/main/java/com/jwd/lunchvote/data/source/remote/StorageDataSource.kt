package com.jwd.lunchvote.data.source.remote

interface StorageDataSource {

  suspend fun uploadFoodImage(foodName: String, image: ByteArray): String
  suspend fun getFoodImageUri(foodName: String): String
  suspend fun uploadProfileImage(userId: String, image: ByteArray): String
  suspend fun getPrivacyPolicyUri(): String
}