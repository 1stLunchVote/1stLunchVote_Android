package com.jwd.lunchvote.remote.source

import com.google.firebase.firestore.FirebaseFirestore
import com.jwd.lunchvote.core.common.error.FoodError
import com.jwd.lunchvote.data.model.FoodData
import com.jwd.lunchvote.data.source.remote.FoodDataSource
import com.jwd.lunchvote.remote.mapper.asData
import com.jwd.lunchvote.remote.model.FoodRemote
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FoodDataSourceImpl @Inject constructor(
  private val fireStore: FirebaseFirestore
): FoodDataSource {

  companion object {
    const val FOOD_PATH = "Food"
    const val COLUMN_ID = "id"
    const val COLUMN_IMAGE_URL = "imageUrl"
    const val COLUMN_NAME = "name"
  }

  override suspend fun getFoodList(): List<FoodData> =
    fireStore
      .collection(FOOD_PATH)
      .get()
      .await()
      .documents
      .map { it.toObject(FoodRemote::class.java)?.asData() ?: throw FoodError.LoadFailure }

  // TODO: 임시
  override suspend fun getFoodTrend(): Pair<FoodData, Float> {
    val foodTrend = fireStore
      .collection(FOOD_PATH)
      .get()
      .await()
      .documents
      .first()
      .toObject(FoodRemote::class.java)?.asData() ?: throw FoodError.LoadFailure

    return foodTrend to 36f
  }
}