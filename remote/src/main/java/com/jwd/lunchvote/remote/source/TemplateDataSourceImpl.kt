package com.jwd.lunchvote.remote.source

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.jwd.lunchvote.data.model.TemplateData
import com.jwd.lunchvote.data.source.remote.TemplateDataSource
import com.jwd.lunchvote.remote.mapper.asData
import com.jwd.lunchvote.remote.mapper.asRemote
import com.jwd.lunchvote.remote.model.TemplateRemote
import com.jwd.lunchvote.remote.util.deleteDocument
import com.jwd.lunchvote.remote.util.whereNotDeleted
import kotlinx.coroutines.tasks.await
import kr.co.inbody.config.error.TemplateError
import javax.inject.Inject

class TemplateDataSourceImpl @Inject constructor(
  private val fireStore: FirebaseFirestore
) : TemplateDataSource {

  companion object {
    private const val COLLECTION_TEMPLATE = "Template"

    private const val COLUMN_USER_ID = "userId"
    private const val COLUMN_NAME = "name"
    private const val COLUMN_LIKED_FOOD_IDS = "likedFoodIds"
    private const val COLUMN_DISLIKED_FOOD_IDS = "dislikedFoodIds"
    private const val COLUMN_CREATED_AT = "createdAt"
    private const val COLUMN_DELETED_AT = "deletedAt"
  }

  override suspend fun addTemplate(
    template: TemplateData
  ): String = 
    fireStore
      .collection(COLLECTION_TEMPLATE)
      .add(template.asRemote())
      .await()
      .get()
      .await()
      .id

  override suspend fun getTemplateById(
    id: String
  ): TemplateData =
    fireStore
      .collection(COLLECTION_TEMPLATE)
      .document(id)
      .get()
      .await()
      .toObject(TemplateRemote::class.java)
      .let { template ->
        if (template == null) throw TemplateError.NoTemplate
        else if (template.deletedAt != null) throw TemplateError.DeletedTemplate
        else template.asData(id)
      }

  override suspend fun getTemplateList(
    userId: String
  ): List<TemplateData> =
    fireStore
      .collection(COLLECTION_TEMPLATE)
      .whereNotDeleted()
      .whereEqualTo(COLUMN_USER_ID, userId)
      .orderBy(COLUMN_CREATED_AT, Query.Direction.DESCENDING)
      .get()
      .await()
      .documents
      .mapNotNull {
        it.toObject(TemplateRemote::class.java)
          ?.asData(it.id)
      }

  override suspend fun updateTemplate(
    template: TemplateData
  ) {
    fireStore
      .collection(COLLECTION_TEMPLATE)
      .document(template.id)
      .set(template.asRemote())
      .await()
  }

  override suspend fun deleteTemplateById(
    id: String
  ) {
    fireStore
      .collection(COLLECTION_TEMPLATE)
      .document(id)
      .deleteDocument()
      .await()
  }
}