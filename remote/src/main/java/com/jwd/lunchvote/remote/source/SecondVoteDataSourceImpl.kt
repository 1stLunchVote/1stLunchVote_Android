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
    const val COLUMN_FOOD_ID = "foodId"
    const val COLUMN_USER_IDS = "userIds"
  }

  override suspend fun createSecondVote(
    loungeId: String,
    foodIds: List<String>
  ) {
    foodIds.forEach { foodId ->
      fireStore
        .collection(SECOND_VOTE_PATH)
        .document(loungeId)
        .collection(COLUMN_FOODS)
        .document(foodId)
        .set(mapOf(COLUMN_FOOD_ID to foodId))
        .await()
    }
  }

  override suspend fun getElectedFoodIdsByLoungeId(
    loungeId: String
  ): List<String> =
    fireStore
      .collection(SECOND_VOTE_PATH)
      .document(loungeId)
      .collection(COLUMN_FOODS)
      .get()
      .await()
      .documents
      .map { it.id }

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
      .document(userId)
      .set(mapOf("message" to "내가 먹고 싶은 메뉴는 바로 너야!"))
      .await()
  }

  override suspend fun getSecondVoteResult(
    loungeId: String
  ): SecondVoteData {
    val foodIds = getElectedFoodIdsByLoungeId(loungeId)

    val foods = foodIds.map { foodId ->
      SecondVoteFoodRemote(
        foodId = foodId,
        userIds = fireStore
          .collection(SECOND_VOTE_PATH)
          .document(loungeId)
          .collection(COLUMN_FOODS)
          .document(foodId)
          .collection(COLUMN_USER_IDS)
          .get()
          .await()
          .documents
          .map { it.id }
      )
    }

    return SecondVoteRemote(
      loungeId = loungeId,
      foods = foods
    ).asData()
  }

}