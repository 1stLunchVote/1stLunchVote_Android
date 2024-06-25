package com.jwd.lunchvote.data.source.remote

import android.net.Uri

interface StorageDataSource {

  suspend fun uploadFoodImage(foodName: String, image: ByteArray)
  suspend fun getFoodImageUri(foodName: String): String
  suspend fun uploadProfileImage(userId: String, image: ByteArray): String
}