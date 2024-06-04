package com.jwd.lunchvote.remote.source

import com.google.firebase.firestore.FirebaseFirestore
import com.jwd.lunchvote.data.model.FirstVoteData
import com.jwd.lunchvote.data.source.remote.FirstVoteDataSource
import com.jwd.lunchvote.remote.mapper.asData
import com.jwd.lunchvote.remote.model.FirstVoteRemote
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirstVoteDataSourceImpl @Inject constructor(
  private val fireStore: FirebaseFirestore
) : FirstVoteDataSource {

  companion object {
    const val FIRST_VOTE_RESULT_PATH = "FirstVote"
    const val FIRST_VOTE_RESULT_MEMBERS_PATH = "members"

    const val COLUMN_LOUNGE_ID = "loungeId"
    const val COLUMN_USER_ID = "userId"
    const val COLUMN_FOOD_SCORE_MAP = "foodScoreMap"
  }
  
  override suspend fun submitVote(
    loungeId: String,
    userId: String,
    likedFoodIds: List<String>, 
    dislikedFoodIds: List<String>
  ) {
    val firstVote = FirstVoteRemote(
      likedFoodIds = likedFoodIds,
      dislikedFoodIds = dislikedFoodIds
    )
      
    fireStore
      .collection(FIRST_VOTE_RESULT_PATH)
      .document(loungeId)
      .collection(FIRST_VOTE_RESULT_MEMBERS_PATH)
      .document(userId)
      .set(firstVote)
      .await()
  }

  override suspend fun getAllFirstVotes(
    loungeId: String
  ): List<FirstVoteData> =
    fireStore
      .collection(FIRST_VOTE_RESULT_PATH)
      .document(loungeId)
      .collection(FIRST_VOTE_RESULT_MEMBERS_PATH)
      .get()
      .await()
      .documents
      .mapNotNull { it.toObject(FirstVoteRemote::class.java)?.asData(loungeId, it.id) }
}