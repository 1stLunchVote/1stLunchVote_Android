package com.jwd.lunchvote.remote.source

import com.google.firebase.firestore.FirebaseFirestore
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

    const val COLUMN_IMAGE = "image"
    const val COLUMN_NAME = "name"
  }

  override suspend fun getAllFood(): List<FoodData> =
    fireStore
      .collection(FOOD_PATH)
      .get()
      .await()
      .documents
      .mapNotNull { it.toObject(FoodRemote::class.java)?.asData(it.id) }

  // TODO: 임시
  override suspend fun getFoodTrend(): Pair<FoodData, Float> =
    fireStore
      .collection(FOOD_PATH)
      .get()
      .await()
      .documents
      .firstNotNullOf { it.toObject(FoodRemote::class.java)?.asData(it.id) } to 36f
}