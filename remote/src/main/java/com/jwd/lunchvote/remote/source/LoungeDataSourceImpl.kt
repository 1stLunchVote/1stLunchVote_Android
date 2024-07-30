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
import kr.co.inbody.config.config.VoteConfig
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

    private const val COLUMN_TIME_LIMIT = "timeLimit"
    private const val COLUMN_MAX_MEMBERS = "maxMembers"
    private const val COLUMN_SECOND_VOTE_CANDIDATES = "secondVoteCandidates"
    private const val COLUMN_MIN_LIKE_FOODS = "minLikeFoods"
    private const val COLUMN_MIN_DISLIKE_FOODS = "minDislikeFoods"
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
      .updateChildren(
        mapOf(
          COLUMN_STATUS to LoungeRemote.STATUS_CREATED,
          COLUMN_MEMBERS to 0,
          COLUMN_TIME_LIMIT to VoteConfig.DEFAULT_TIME_LIMIT,
          COLUMN_MAX_MEMBERS to VoteConfig.DEFAULT_MAX_MEMBERS,
          COLUMN_SECOND_VOTE_CANDIDATES to VoteConfig.DEFAULT_SECOND_VOTE_CANDIDATES,
          COLUMN_MIN_LIKE_FOODS to null,
          COLUMN_MIN_DISLIKE_FOODS to null
        )
      )

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

  override suspend fun updateLounge(
    lounge: LoungeData
  ) {
    database
      .getReference(REFERENCE_LOUNGE)
      .child(lounge.id)
      .setValue(lounge.asRemote())
      .await()
  }
}