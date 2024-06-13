package com.jwd.lunchvote.remote.source

import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.values
import com.google.firebase.firestore.FirebaseFirestore
import com.jwd.lunchvote.core.common.error.LoungeError
import com.jwd.lunchvote.data.model.LoungeData
import com.jwd.lunchvote.data.source.remote.LoungeDataSource
import com.jwd.lunchvote.remote.mapper.asData
import com.jwd.lunchvote.remote.mapper.asLoungeDataStatus
import com.jwd.lunchvote.remote.mapper.asRemote
import com.jwd.lunchvote.remote.model.LoungeRemote
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject

class LoungeDataSourceImpl @Inject constructor(
  private val database: FirebaseDatabase,
  private val fireStore: FirebaseFirestore
) : LoungeDataSource {

  companion object {
    const val LOUNGE_PATH = "Lounge"

    const val LOUNGE_STATUS = "status"
    const val LOUNGE_MEMBERS = "members"

    const val LOUNGE_ELECTED_FOOD_ID = "electedFoodId"
  }

  override suspend fun checkLoungeExistById(
    id: String
  ): Boolean =
    database
      .getReference(LOUNGE_PATH)
      .child(id)
      .get()
      .await()
      .exists()


  override suspend fun createLounge(): String {
    val loungeId = UUID.randomUUID().toString()
    database
      .getReference(LOUNGE_PATH)
      .child(loungeId)
      .apply {
        child(LOUNGE_STATUS)
          .setValue(LoungeRemote.STATUS_CREATED)
          .await()
        child(LOUNGE_MEMBERS)
          .setValue(0)
          .await()
      }

    return loungeId
  }

  override fun getLoungeStatusFlowById(
    id: String
  ): Flow<LoungeData.Status> =
    database
      .getReference(LOUNGE_PATH)
      .child(id)
      .child(LOUNGE_STATUS)
      .values<String>()
      .mapNotNull { status -> status?.asLoungeDataStatus() }

  override suspend fun getLoungeById(
    id: String
  ): LoungeData =
    database
      .getReference(LOUNGE_PATH)
      .child(id)
      .get()
      .await()
      .getValue(LoungeRemote::class.java)
      ?.asData(id) ?: throw LoungeError.NoLounge

  override suspend fun joinLoungeById(
    id: String
  ) {
    database
      .getReference(LOUNGE_PATH)
      .child(id)
      .child(LOUNGE_MEMBERS)
      .apply {
        val members = get().await().value as Long
        if (members >= 6) throw LoungeError.FullMember
        setValue(members + 1).await()
      }
  }

  override suspend fun exitLoungeById(
    id: String
  ) {
    database
      .getReference(LOUNGE_PATH)
      .child(id)
      .child(LOUNGE_MEMBERS)
      .apply {
        val members = get().await().value as Long
        if (members <= 0) throw LoungeError.NoMember
        setValue(members - 1).await()
      }
  }

  override suspend fun quitLoungeById(
    id: String
  ) {
    database
      .getReference(LOUNGE_PATH)
      .child(id)
      .apply {
        child(LOUNGE_STATUS)
          .setValue(LoungeRemote.STATUS_QUIT)
          .await()

        child(LOUNGE_MEMBERS)
          .apply {
            val members = get().await().value as Long
            if (members <= 0) throw LoungeError.NoMember
            setValue(members - 1).await()
          }
      }
  }

  override suspend fun updateLoungeStatusById(
    id: String,
    status: LoungeData.Status
  ) {
    database
      .getReference(LOUNGE_PATH)
      .child(id)
      .child(LOUNGE_STATUS)
      .setValue(status.asRemote())
      .await()
  }

  override suspend fun saveVoteResultById(
    id: String,
    electedFoodId: String
  ) {
    fireStore
      .collection(LOUNGE_PATH)
      .document(id)
      .set(mapOf(LOUNGE_ELECTED_FOOD_ID to electedFoodId))
      .await()
  }

  override suspend fun getAllVoteResults(): List<String> =
    fireStore
      .collection(LOUNGE_PATH)
      .get()
      .await()
      .documents
      .mapNotNull { it.getString(LOUNGE_ELECTED_FOOD_ID) }
}