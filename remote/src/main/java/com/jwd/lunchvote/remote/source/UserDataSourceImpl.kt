package com.jwd.lunchvote.remote.source

import com.google.firebase.firestore.FirebaseFirestore
import com.jwd.lunchvote.data.model.UserData
import com.jwd.lunchvote.data.source.remote.UserDataSource
import com.jwd.lunchvote.remote.mapper.asData
import com.jwd.lunchvote.remote.mapper.asRemote
import com.jwd.lunchvote.remote.model.UserRemote
import com.jwd.lunchvote.remote.util.whereNotDeleted
import kotlinx.coroutines.tasks.await
import kr.co.inbody.config.error.UserError
import javax.inject.Inject

class UserDataSourceImpl @Inject constructor(
  private val fireStore: FirebaseFirestore
) : UserDataSource {

  companion object {
    private const val COLLECTION_USER = "User"

    private const val COLUMN_EMAIL = "email"
    private const val COLUMN_NAME = "name"
    private const val COLUMN_PROFILE_IMAGE = "profileImage"
    private const val COLUMN_CREATED_AT = "createdAt"
    private const val COLUMN_DELETED_AT = "deletedAt"
  }

  override suspend fun checkEmailExists(
    email: String
  ): Boolean =
    fireStore
      .collection(COLLECTION_USER)
      .whereNotDeleted()
      .whereEqualTo(COLUMN_EMAIL, email)
      .get()
      .await()
      .isEmpty
      .not()

  override suspend fun checkNameExists(
    name: String
  ): Boolean =
    fireStore
      .collection(COLLECTION_USER)
      .whereNotDeleted()
      .whereEqualTo(COLUMN_NAME, name)
      .get()
      .await()
      .isEmpty
      .not()

  override suspend fun createUser(
    user: UserData
  ): String =
    fireStore
      .collection(COLLECTION_USER)
      .document(user.id)
      .apply {
        set(user.asRemote())
          .await()
      }
      .id

  override suspend fun getUserById(
    id: String
  ): UserData =
    fireStore
      .collection(COLLECTION_USER)
      .document(id)
      .get()
      .await()
      .toObject(UserRemote::class.java)
      .let { user ->
        user?.asData(id) ?: throw UserError.NoUser
      }


  override suspend fun updateUser(
    user: UserData
  ) {
    fireStore
      .collection(COLLECTION_USER)
      .document(user.id)
      .set(user.asRemote())
      .await()
  }
}