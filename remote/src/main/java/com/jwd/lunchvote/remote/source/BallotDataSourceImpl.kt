package com.jwd.lunchvote.remote.source

import com.google.firebase.database.FirebaseDatabase
import com.jwd.lunchvote.data.model.FirstBallotData
import com.jwd.lunchvote.data.model.SecondBallotData
import com.jwd.lunchvote.data.source.remote.BallotDataSource
import com.jwd.lunchvote.remote.mapper.asData
import com.jwd.lunchvote.remote.mapper.asRemote
import com.jwd.lunchvote.remote.model.FirstBallotRemote
import com.jwd.lunchvote.remote.model.SecondBallotRemote
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class BallotDataSourceImpl @Inject constructor(
  private val database: FirebaseDatabase
) : BallotDataSource {

  companion object {
    private const val REFERENCE_BALLOT = "Ballot"

    private const val CHILD_FIRST_BALLOT = "First"
    private const val CHILD_SECOND_BALLOT = "Second"
  }

  override suspend fun submitFirstBallot(
    firstBallot: FirstBallotData
  ) {
    database
      .getReference(REFERENCE_BALLOT)
      .child(firstBallot.loungeId)
      .child(CHILD_FIRST_BALLOT)
      .child(firstBallot.userId)
      .setValue(firstBallot.asRemote())
  }

  override suspend fun getAllFirstBallotByLoungeId(
    loungeId: String
  ): List<FirstBallotData> =
    database
      .getReference(REFERENCE_BALLOT)
      .child(loungeId)
      .child(CHILD_FIRST_BALLOT)
      .get()
      .await()
      .children
      .mapNotNull {
        it.getValue(FirstBallotRemote::class.java)
          ?.asData(loungeId, it.key!!)
      }

  override suspend fun submitSecondBallot(
    secondBallot: SecondBallotData
  ) {
    database
      .getReference(REFERENCE_BALLOT)
      .child(secondBallot.loungeId)
      .child(CHILD_SECOND_BALLOT)
      .child(secondBallot.userId)
      .setValue(secondBallot.asRemote())
  }

  override suspend fun getAllSecondBallotByLoungeId(
    loungeId: String
  ): List<SecondBallotData> =
    database
      .getReference(REFERENCE_BALLOT)
      .child(loungeId)
      .child(CHILD_SECOND_BALLOT)
      .get()
      .await()
      .children
      .mapNotNull {
        it.getValue(SecondBallotRemote::class.java)
          ?.asData(loungeId, it.key!!)
      }
}