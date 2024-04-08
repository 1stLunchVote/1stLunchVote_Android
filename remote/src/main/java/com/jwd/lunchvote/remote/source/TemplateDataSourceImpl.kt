package com.jwd.lunchvote.remote.source

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.jwd.lunchvote.data.model.TemplateData
import com.jwd.lunchvote.data.source.remote.TemplateDataSource
import com.jwd.lunchvote.remote.mapper.asData
import com.jwd.lunchvote.remote.model.TemplateRemote
import kotlinx.coroutines.tasks.await
import java.time.LocalDateTime
import javax.inject.Inject

class TemplateDataSourceImpl @Inject constructor(
  private val fireStore: FirebaseFirestore
): TemplateDataSource {

  companion object {
    const val TEMPLATE_PATH = "Template"
    const val COLUMN_ID = "id"
    const val COLUMN_USER_ID = "userId"
    const val COLUMN_NAME = "name"
    const val COLUMN_LIKE = "like"
    const val COLUMN_DISLIKE = "dislike"
    const val COLUMN_CREATED_AT = "createdAt"
    const val COLUMN_DELETED_AT = "deletedAt"
  }

  override suspend fun getTemplateList(
    userId: String
  ): List<TemplateData> =
    fireStore
      .collection(TEMPLATE_PATH)
      .whereEqualTo(COLUMN_USER_ID, userId)
      .whereEqualTo(COLUMN_DELETED_AT, null)
      .orderBy(COLUMN_CREATED_AT, Query.Direction.DESCENDING)
      .get()
      .await()
      .documents
      .map { it.toObject(TemplateRemote::class.java)?.asData() ?: throw Exception("TODO ERROR") }
      .sortedByDescending { it.createdAt }

  override suspend fun addTemplate(
    template: TemplateData
  ): TemplateData =
    fireStore
      .collection(TEMPLATE_PATH)
      .add(template)
      .await()
      .get()
      .await()
      .toObject(TemplateRemote::class.java)?.asData() ?: throw Exception("TODO ERROR")

  override suspend fun getTemplate(
    id: String
  ): TemplateData =
    fireStore
      .collection(TEMPLATE_PATH)
      .document(id)
      .get()
      .await()
      .toObject(TemplateRemote::class.java)?.asData() ?: throw Exception("TODO ERROR")

  override suspend fun editTemplate(
    template: TemplateData
  ): TemplateData {
    fireStore
      .collection(TEMPLATE_PATH)
      .document(template.id)
      .set(template)
      .await()

    return template
  }

  override suspend fun deleteTemplate(
    id: String
  ) {
    fireStore
      .collection(TEMPLATE_PATH)
      .document(id)
      .update(COLUMN_DELETED_AT, LocalDateTime.now().toString())
      .await()
  }
}