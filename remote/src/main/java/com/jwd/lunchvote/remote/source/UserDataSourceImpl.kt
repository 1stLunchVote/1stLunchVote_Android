package com.jwd.lunchvote.remote.source

import com.google.firebase.firestore.FirebaseFirestore
import com.jwd.lunchvote.data.model.UserData
import com.jwd.lunchvote.data.source.remote.UserDataSource
import com.jwd.lunchvote.remote.mapper.asRemote
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class UserDataSourceImpl @Inject constructor(
  private val fireStore: FirebaseFirestore
): UserDataSource {

  companion object {
    const val USER_PATH = "User"
    const val COLUMN_EMAIL = "email"
    const val COLUMN_NAME = "name"
    const val COLUMN_PROFILE_IMAGE_URL = "profileImageUrl"
    const val COLUMN_CREATED_AT = "createdAt"
  }

  override suspend fun createUser(
    user: UserData
  ): String {
    fireStore
      .collection(USER_PATH)
      .document(user.id)
      .set(user.asRemote())
      .await()

    return user.id
  }
}