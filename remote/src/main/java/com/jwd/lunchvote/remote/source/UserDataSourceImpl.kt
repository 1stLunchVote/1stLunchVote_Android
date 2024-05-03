package com.jwd.lunchvote.remote.source

import com.google.firebase.firestore.FirebaseFirestore
import com.jwd.lunchvote.core.common.error.LoginError
import com.jwd.lunchvote.data.model.UserData
import com.jwd.lunchvote.data.source.remote.UserDataSource
import com.jwd.lunchvote.remote.mapper.asData
import com.jwd.lunchvote.remote.mapper.asRemote
import com.jwd.lunchvote.remote.model.UserRemote
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

  override suspend fun checkUserExists(
    email: String
  ): Boolean =
    fireStore
      .collection(USER_PATH)
      .whereEqualTo(COLUMN_EMAIL, email)
      .get()
      .await()
      .isEmpty
      .not()

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

  override suspend fun getUserById(
    id: String
  ): UserData =
    fireStore
      .collection(USER_PATH)
      .document(id)
      .get()
      .await()
      .toObject(UserRemote::class.java)
      ?.asData(id) ?: throw LoginError.NoUser

  override suspend fun updateUser(
    user: UserData
  ) {
    fireStore
      .collection(USER_PATH)
      .document(user.id)
      .set(user.asRemote())
      .await()
  }
}