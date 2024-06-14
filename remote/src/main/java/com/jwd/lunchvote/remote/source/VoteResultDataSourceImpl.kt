package com.jwd.lunchvote.remote.source

import com.google.firebase.firestore.FirebaseFirestore
import com.jwd.lunchvote.core.common.error.VoteError
import com.jwd.lunchvote.data.model.VoteResultData
import com.jwd.lunchvote.data.source.remote.VoteResultDataSource
import com.jwd.lunchvote.remote.mapper.asData
import com.jwd.lunchvote.remote.mapper.asRemote
import com.jwd.lunchvote.remote.model.VoteResultRemote
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class VoteResultDataSourceImpl @Inject constructor(
  private val fireStore: FirebaseFirestore
): VoteResultDataSource {

  companion object {
    private const val COLLECTION_VOTE_RESULT = "VoteResult"

    private const val COLUMN_LOUNGE_ID = "loungeId"
    private const val COLUMN_FOOD_ID = "foodId"
    private const val COLUMN_VOTE_COUNT = "voteCount"
  }

  override suspend fun saveVoteResult(
    voteResult: VoteResultData
  ) {
    fireStore
      .collection(COLLECTION_VOTE_RESULT)
      .document(voteResult.loungeId)
      .set(voteResult.asRemote())
      .await()
  }

  override suspend fun getVoteResultByLoungeId(
    loungeId: String
  ): VoteResultData =
    fireStore
      .collection(COLLECTION_VOTE_RESULT)
      .document(loungeId)
      .get()
      .await()
      .toObject(VoteResultRemote::class.java)
      ?.asData(loungeId) ?: throw VoteError.NoVoteResult

  override suspend fun getAllVoteResults(): List<VoteResultData> =
    fireStore
      .collection(COLLECTION_VOTE_RESULT)
      .get()
      .await()
      .documents
      .mapNotNull {
        it.toObject(VoteResultRemote::class.java)?.asData(it.id)
      }
}