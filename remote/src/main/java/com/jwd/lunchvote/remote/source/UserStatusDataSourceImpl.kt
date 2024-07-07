package com.jwd.lunchvote.remote.source

import com.google.firebase.Timestamp
import com.google.firebase.database.FirebaseDatabase
import com.jwd.lunchvote.data.model.UserStatusData
import com.jwd.lunchvote.data.source.remote.UserStatusDataSource
import com.jwd.lunchvote.remote.mapper.asData
import com.jwd.lunchvote.remote.model.UserStatusRemote
import com.jwd.lunchvote.remote.util.toLong
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class UserStatusDataSourceImpl @Inject constructor(
  private val database: FirebaseDatabase
): UserStatusDataSource {

  companion object {
    private const val REFERENCE_USER_STATUS = "UserStatus"

    private const val COLUMN_LAST_ONLINE = "lastOnline"
    private const val COLUMN_LOUNGE_ID = "loungeId"
  }

  override suspend fun createUserStatus(
    userId: String
  ) {
    val userStatus = UserStatusData(
      userId = userId,
      lastOnline = null,
      loungeId = null
    )
    database
      .getReference(REFERENCE_USER_STATUS)
      .child(userId)
      .setValue(userStatus)
  }

  override suspend fun setUserOnline(
    userId: String
  ) {
    database
      .getReference(REFERENCE_USER_STATUS)
      .child(userId)
      .child(COLUMN_LAST_ONLINE)
      .setValue(Timestamp.now().toLong())
  }

  override suspend fun setUserOffline(
    userId: String
  ) {
    database
      .getReference(REFERENCE_USER_STATUS)
      .child(userId)
      .child(COLUMN_LAST_ONLINE)
      .setValue(null)
  }

  override suspend fun setUserLounge(
    userId: String,
    loungeId: String?
  ) {
    database
      .getReference(REFERENCE_USER_STATUS)
      .child(userId)
      .child(COLUMN_LOUNGE_ID)
      .setValue(loungeId)
  }

  override suspend fun getUserStatus(
    userId: String
  ): UserStatusData? =
    database
      .getReference(REFERENCE_USER_STATUS)
      .child(userId)
      .get()
      .await()
      .getValue(UserStatusRemote::class.java)
      ?.asData(userId = userId)
}