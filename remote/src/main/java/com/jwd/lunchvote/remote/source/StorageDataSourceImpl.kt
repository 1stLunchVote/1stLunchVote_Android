package com.jwd.lunchvote.remote.source

import com.google.firebase.storage.FirebaseStorage
import com.jwd.lunchvote.data.source.remote.StorageDataSource
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class StorageDataSourceImpl @Inject constructor(
  private val storage: FirebaseStorage
): StorageDataSource {

  companion object {
    const val PROFILE_IMAGE_PATH = "Profile"
  }

  override suspend fun uploadProfileImage(
    userId: String,
    image: ByteArray
  ): String =
    storage
      .reference
      .child(PROFILE_IMAGE_PATH)
      .child(userId)
      .putBytes(image)
      .await()
      .storage
      .downloadUrl
      .await()
      .toString()
}