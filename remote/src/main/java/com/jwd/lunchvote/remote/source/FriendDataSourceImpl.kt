package com.jwd.lunchvote.remote.source

import com.google.firebase.firestore.FirebaseFirestore
import com.jwd.lunchvote.data.source.FriendDataSource
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


}