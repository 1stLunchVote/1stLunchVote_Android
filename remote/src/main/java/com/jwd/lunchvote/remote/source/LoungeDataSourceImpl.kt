package com.jwd.lunchvote.remote.source

import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.values
import com.jwd.lunchvote.data.model.LoungeData
import com.jwd.lunchvote.data.source.remote.LoungeDataSource
import com.jwd.lunchvote.remote.mapper.asData
import com.jwd.lunchvote.remote.mapper.asLoungeDataStatus
import com.jwd.lunchvote.remote.mapper.asRemote
import com.jwd.lunchvote.remote.model.LoungeRemote
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.tasks.await
import kr.co.inbody.config.error.LoungeError
import java.util.UUID
import javax.inject.Inject

class LoungeDataSourceImpl @Inject constructor(
  private val database: FirebaseDatabase
) : LoungeDataSource {

  companion object {
    private const val REFERENCE_LOUNGE = "Lounge"

    private const val COLUMN_STATUS = "status"
    private const val COLUMN_MEMBERS = "members"
  }

  override suspend fun checkLoungeExistById(
    id: String
  ): Boolean =
    database
      .getReference(REFERENCE_LOUNGE)
      .child(id)
      .get()
      .await()
      .exists()


  override suspend fun createLounge(): String {
    val loungeId = UUID.randomUUID().toString()
    database
      .getReference(REFERENCE_LOUNGE)
      .child(loungeId)
      .apply {
        child(COLUMN_STATUS)
          .setValue(LoungeRemote.STATUS_CREATED)
          .await()
        child(COLUMN_MEMBERS)
          .setValue(0)
          .await()
      }

    return loungeId
  }

  override fun getLoungeStatusFlowById(
    id: String
  ): Flow<LoungeData.Status> =
    database
      .getReference(REFERENCE_LOUNGE)
      .child(id)
      .child(COLUMN_STATUS)
      .values<String>()
      .mapNotNull { status -> status?.asLoungeDataStatus() }

  override suspend fun getLoungeById(
    id: String
  ): LoungeData =
    database
      .getReference(REFERENCE_LOUNGE)
      .child(id)
      .get()
      .await()
      .getValue(LoungeRemote::class.java)
      ?.asData(id) ?: throw LoungeError.NoLounge

  override suspend fun joinLoungeById(
    id: String
  ) {
    database
      .getReference(REFERENCE_LOUNGE)
      .child(id)
      .child(COLUMN_MEMBERS)
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
      .getReference(REFERENCE_LOUNGE)
      .child(id)
      .child(COLUMN_MEMBERS)
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
      .getReference(REFERENCE_LOUNGE)
      .child(id)
      .apply {
        child(COLUMN_STATUS)
          .setValue(LoungeRemote.STATUS_QUIT)
          .await()
      }
  }

  override suspend fun updateLoungeStatusById(
    id: String,
    status: LoungeData.Status
  ) {
    database
      .getReference(REFERENCE_LOUNGE)
      .child(id)
      .child(COLUMN_STATUS)
      .setValue(status.asRemote())
      .await()
  }
}