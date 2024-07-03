package com.jwd.lunchvote.remote.source

import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.jwd.lunchvote.data.model.FriendData
import com.jwd.lunchvote.data.source.remote.FriendDataSource
import com.jwd.lunchvote.remote.mapper.asData
import com.jwd.lunchvote.remote.model.FriendRemote
import com.jwd.lunchvote.remote.util.whereNotDeleted
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FriendDataSourceImpl @Inject constructor(
  private val fireStore: FirebaseFirestore
): FriendDataSource {

  companion object {
    private const val COLLECTION_FRIEND = "Friend"

    private const val COLUMN_USER_ID = "userId"
    private const val COLUMN_FRIEND_ID = "friendId"
    private const val COLUMN_CREATED_AT = "createdAt"
    private const val COLUMN_MATCHED_AT = "matchedAt"
    private const val COLUMN_DELETED_AT = "deletedAt"
  }

  override suspend fun requestFriend(
    userId: String,
    friendId: String
  ) {
    val friend = FriendRemote(
      userId = userId,
      friendId = friendId
    )
    fireStore
      .collection(COLLECTION_FRIEND)
      .add(friend)
  }

  override suspend fun acceptFriend(
    id: String
  ) {
    fireStore
      .collection(COLLECTION_FRIEND)
      .document(id)
      .update(COLUMN_MATCHED_AT, Timestamp.now())
  }

  override suspend fun rejectFriend(
    id: String
  ) {
    fireStore
      .collection(COLLECTION_FRIEND)
      .document(id)
      .update(COLUMN_DELETED_AT, Timestamp.now())
  }

  override suspend fun getSentFriendRequests(
    userId: String
  ): List<FriendData> =
    fireStore
      .collection(COLLECTION_FRIEND)
      .whereNotDeleted()
      .whereEqualTo(COLUMN_MATCHED_AT, null)
      .whereEqualTo(COLUMN_USER_ID, userId)
      .get()
      .await()
      .mapNotNull {
        it.toObject(FriendRemote::class.java)
          .asData(it.id)
      }

  override suspend fun getReceivedFriendRequests(
    userId: String
  ): List<FriendData> =
    fireStore
      .collection(COLLECTION_FRIEND)
      .whereNotDeleted()
      .whereEqualTo(COLUMN_MATCHED_AT, null)
      .whereEqualTo(COLUMN_FRIEND_ID, userId)
      .get()
      .await()
      .mapNotNull {
        it.toObject(FriendRemote::class.java)
          .asData(it.id)
      }

  override suspend fun getFriends(
    userId: String
  ): List<String> =
    fireStore
      .collection(COLLECTION_FRIEND)
      .whereNotDeleted()
      .whereNotEqualTo(COLUMN_MATCHED_AT, null)
      .let { reference ->
        val sentFriends = reference.whereEqualTo(COLUMN_USER_ID, userId)
          .get()
          .await()
          .toObjects(FriendRemote::class.java)
          .map { it.friendId }

        val receivedFriends = reference.whereEqualTo(COLUMN_FRIEND_ID, userId)
          .get()
          .await()
          .toObjects(FriendRemote::class.java)
          .map { it.userId }

        sentFriends + receivedFriends
      }

  override suspend fun deleteFriend(
    userId: String,
    friendId: String
  ) {
    fireStore
      .collection(COLLECTION_FRIEND)
      .whereEqualTo(COLUMN_USER_ID, userId)
      .whereEqualTo(COLUMN_FRIEND_ID, friendId)
      .get()
      .await()
      .documents
      .forEach { it.reference.delete() }
  }
}