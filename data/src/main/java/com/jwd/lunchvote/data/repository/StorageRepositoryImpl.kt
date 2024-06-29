package com.jwd.lunchvote.data.repository

import com.jwd.lunchvote.data.source.remote.StorageDataSource
import com.jwd.lunchvote.domain.repository.StorageRepository
import javax.inject.Inject

class StorageRepositoryImpl @Inject constructor(
  private val storageDataSource: StorageDataSource
): StorageRepository {

  override suspend fun uploadFoodImage(foodName: String, image: ByteArray): String =
    storageDataSource.uploadFoodImage(foodName, image)

  override suspend fun getFoodImageUri(foodName: String): String =
    storageDataSource.getFoodImageUri(foodName)

  override suspend fun uploadProfileImage(userId: String, image: ByteArray): String =
    storageDataSource.uploadProfileImage(userId, image)
}