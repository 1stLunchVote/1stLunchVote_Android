package com.jwd.lunchvote.remote.source

import com.google.firebase.firestore.FirebaseFirestore
import com.jwd.lunchvote.data.model.FoodData
import com.jwd.lunchvote.data.source.remote.FoodDataSource
import com.jwd.lunchvote.data.source.remote.StorageDataSource
import com.jwd.lunchvote.remote.mapper.asData
import com.jwd.lunchvote.remote.mapper.asRemote
import com.jwd.lunchvote.remote.model.FoodRemote
import kotlinx.coroutines.tasks.await
import kr.co.inbody.config.error.FoodError
import java.io.File
import javax.inject.Inject

class FoodDataSourceImpl @Inject constructor(
  private val fireStore: FirebaseFirestore,
  private val storageDataSource: StorageDataSource
) : FoodDataSource {

  companion object {
    private const val COLLECTION_FOOD = "Food"

    private const val COLUMN_NAME = "name"
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
        it.toObject(FoodRemote::class.java)
          ?.let { food ->
            val image = storageDataSource.getFoodImage(food.name)
            food.asData(it.id, image)
          } ?: throw FoodError.NoFood
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
      ?.let {
        val image = storageDataSource.getFoodImage(it.name)
        it.asData(id, image)
      } ?: throw FoodError.NoFood
}