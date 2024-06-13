package com.jwd.lunchvote.remote.source

import com.google.firebase.firestore.FirebaseFirestore
import com.jwd.lunchvote.data.model.SecondVoteData
import com.jwd.lunchvote.data.source.remote.SecondVoteDataSource
import com.jwd.lunchvote.remote.mapper.asData
import com.jwd.lunchvote.remote.model.SecondVoteFoodRemote
import com.jwd.lunchvote.remote.model.SecondVoteRemote
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class SecondVoteDataSourceImpl @Inject constructor(
  private val fireStore: FirebaseFirestore
) : SecondVoteDataSource {

  companion object {
    const val SECOND_VOTE_PATH = "SecondVote"

    const val COLUMN_LOUNGE_ID = "loungeId"
    const val COLUMN_FOODS = "foods"
    const val COLUMN_FOODS_ID = "foodId"
    const val COLUMN_USER_IDS = "userIds"
  }

  override suspend fun createSecondVote(
    loungeId: String,
    foodIds: List<String>
  ) {
    fireStore
      .collection(SECOND_VOTE_PATH)
      .document(loungeId)
      .set(
        mapOf(
          COLUMN_LOUNGE_ID to loungeId,
          COLUMN_FOODS to foodIds
        )
      )
      .await()
  }

  override suspend fun getElectedFoodIdsByLoungeId(
    loungeId: String
  ): List<String> =
    fireStore
      .collection(SECOND_VOTE_PATH)
      .document(loungeId)
      .get()
      .await()
      .get(COLUMN_FOODS, List::class.java)
      ?.map { it as String }
      ?: emptyList()

  override suspend fun submitVote(
    loungeId: String,
    userId: String,
    foodId: String
  ) {
    fireStore
      .collection(SECOND_VOTE_PATH)
      .document(loungeId)
      .collection(COLUMN_FOODS)
      .document(foodId)
      .collection(COLUMN_USER_IDS)
      .add(userId)
      .await()
  }

  override suspend fun getSecondVoteResult(
    loungeId: String
  ): SecondVoteData {
    val foods = fireStore
      .collection(SECOND_VOTE_PATH)
      .document(loungeId)
      .collection(COLUMN_FOODS)
      .get()
      .await()
      .documents
      .mapNotNull { it.toObject(SecondVoteFoodRemote::class.java) }

    return SecondVoteRemote(
      loungeId = loungeId,
      foods = foods
    ).asData()
  }

}