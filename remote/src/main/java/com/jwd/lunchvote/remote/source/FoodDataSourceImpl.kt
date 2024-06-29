package com.jwd.lunchvote.remote.source

import com.google.firebase.firestore.FirebaseFirestore
import com.jwd.lunchvote.data.model.FoodData
import com.jwd.lunchvote.data.source.remote.FoodDataSource
import com.jwd.lunchvote.remote.mapper.asData
import com.jwd.lunchvote.remote.mapper.asRemote
import com.jwd.lunchvote.remote.model.FoodRemote
import kotlinx.coroutines.tasks.await
import kr.co.inbody.config.error.FoodError
import javax.inject.Inject

class FoodDataSourceImpl @Inject constructor(
  private val fireStore: FirebaseFirestore
) : FoodDataSource {

  companion object {
    private const val COLLECTION_FOOD = "Food"

    private const val COLUMN_NAME = "name"
    private const val COLUMN_IMAGE_URL = "imageUrl"
  }

  override suspend fun createFood(food: FoodData) {
    fireStore
      .collection(COLLECTION_FOOD)
      .add(food.asRemote())
      .await()
  }

  override suspend fun getAllFood(): List<FoodData> =
    fireStore
      .collection(COLLECTION_FOOD)
      .get()
      .await()
      .documents
      .mapNotNull {
        it.toObject(FoodRemote::class.java)?.asData(it.id)
      }

  override suspend fun getFoodById(
    id: String
  ): FoodData =
    fireStore
      .collection(COLLECTION_FOOD)
      .document(id)
      .get()
      .await()
      .toObject(FoodRemote::class.java)
      ?.asData(id) ?: throw FoodError.NoFood
}