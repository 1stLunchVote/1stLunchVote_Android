package com.jwd.lunchvote.remote.source

import com.google.firebase.database.FirebaseDatabase
import com.jwd.lunchvote.core.common.error.VoteError
import com.jwd.lunchvote.data.model.FirstVoteResultData
import com.jwd.lunchvote.data.model.SecondVoteResultData
import com.jwd.lunchvote.data.source.remote.VoteResultDataSource
import com.jwd.lunchvote.remote.mapper.asData
import com.jwd.lunchvote.remote.mapper.asRemote
import com.jwd.lunchvote.remote.model.FirstVoteResultRemote
import com.jwd.lunchvote.remote.model.SecondVoteResultRemote
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class VoteResultDataSourceImpl @Inject constructor(
  private val database: FirebaseDatabase
): VoteResultDataSource {

  companion object {
    private const val REFERENCE_VOTE_RESULT = "VoteResult"

    private const val CHILD_FIRST_VOTE_RESULT = "First"
    private const val CHILD_SECOND_VOTE_RESULT = "Second"
  }

  override suspend fun saveFirstVoteResult(
    firstVoteResult: FirstVoteResultData
  ) {
    database
      .getReference(REFERENCE_VOTE_RESULT)
      .child(firstVoteResult.loungeId)
      .child(CHILD_FIRST_VOTE_RESULT)
      .setValue(firstVoteResult.asRemote())
  }

  override suspend fun getFirstVoteResultByLoungeId(
    loungeId: String
  ): FirstVoteResultData =
    database
      .getReference(REFERENCE_VOTE_RESULT)
      .child(loungeId)
      .child(CHILD_FIRST_VOTE_RESULT)
      .get()
      .await()
      .getValue(FirstVoteResultRemote::class.java)
      ?.asData(loungeId) ?: throw VoteError.NoVoteResult

  override suspend fun saveSecondVoteResult(
    secondVoteResult: SecondVoteResultData
  ) {
    database
      .getReference(REFERENCE_VOTE_RESULT)
      .child(secondVoteResult.loungeId)
      .child(CHILD_SECOND_VOTE_RESULT)
      .setValue(secondVoteResult.asRemote())
  }

  override suspend fun getSecondVoteResultByLoungeId(
    loungeId: String
  ): SecondVoteResultData =
    database
      .getReference(REFERENCE_VOTE_RESULT)
      .child(loungeId)
      .child(CHILD_SECOND_VOTE_RESULT)
      .get()
      .await()
      .getValue(SecondVoteResultRemote::class.java)
      ?.asData(loungeId) ?: throw VoteError.NoVoteResult

  override suspend fun getAllVoteResults(): List<SecondVoteResultData> =
    database
      .getReference(REFERENCE_VOTE_RESULT)
      .get()
      .await()
      .children
      .mapNotNull {
        it.getValue(SecondVoteResultRemote::class.java)
          ?.asData(it.key!!)
      }
}