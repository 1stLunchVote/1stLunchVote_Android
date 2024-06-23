package com.jwd.lunchvote.remote.source

import com.google.firebase.storage.FirebaseStorage
import com.jwd.lunchvote.data.source.remote.StorageDataSource
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class StorageDataSourceImpl @Inject constructor(
  private val storage: FirebaseStorage
) : StorageDataSource {

  companion object {
    const val STORAGE_REFERENCE_FOOD = "Food"
    const val STORAGE_REFERENCE_PROFILE = "Profile"
  }

  override suspend fun uploadFoodImage(
    foodName: String,
    image: ByteArray
  ) {
    storage
      .reference
      .child(STORAGE_REFERENCE_FOOD)
      .child(foodName)
      .putBytes(image)
      .await()
  }

  override suspend fun getFoodImage(foodName: String): ByteArray =
    storage
      .reference
      .child(STORAGE_REFERENCE_FOOD)
      .child(foodName)
      .getBytes(1024 * 1024)
      .await()

  override suspend fun uploadProfileImage(
    userId: String,
    image: ByteArray
  ): String =
    storage
      .reference
      .child(STORAGE_REFERENCE_PROFILE)
      .child(userId)
      .putBytes(image)
      .await()
      .storage
      .downloadUrl
      .await()
      .toString()
}