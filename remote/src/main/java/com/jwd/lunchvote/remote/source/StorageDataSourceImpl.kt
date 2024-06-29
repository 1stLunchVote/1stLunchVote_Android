package com.jwd.lunchvote.remote.source

import com.google.firebase.storage.FirebaseStorage
import com.jwd.lunchvote.data.source.remote.StorageDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class StorageDataSourceImpl @Inject constructor(
  private val storage: FirebaseStorage,
  private val dispatcher: CoroutineDispatcher
) : StorageDataSource {

  companion object {
    const val STORAGE_REFERENCE_FOOD = "Food"
    const val STORAGE_REFERENCE_PROFILE = "Profile"
  }

  override suspend fun uploadFoodImage(
    foodName: String,
    image: ByteArray
  ): String = withContext(dispatcher) {
    storage
      .reference
      .child(STORAGE_REFERENCE_FOOD)
      .child(foodName)
      .putBytes(image)
      .await()
      .storage
      .downloadUrl
      .await()
      .toString()
  }

  override suspend fun getFoodImageUri(
    foodName: String
  ): String = withContext(dispatcher) {
    storage
      .reference
      .child(STORAGE_REFERENCE_FOOD)
      .child(foodName)
      .downloadUrl
      .await()
      .toString()
  }

  override suspend fun uploadProfileImage(
    userId: String,
    image: ByteArray
  ): String =
    withContext(dispatcher) {
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
}