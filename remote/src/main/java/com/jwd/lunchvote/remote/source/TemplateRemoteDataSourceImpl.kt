package com.jwd.lunchvote.remote.source

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.jwd.lunchvote.data.source.remote.TemplateRemoteDataSource
import com.jwd.lunchvote.domain.entity.Food
import com.jwd.lunchvote.domain.entity.Template
import kotlinx.coroutines.tasks.await
import java.time.LocalDateTime
import java.util.UUID
import javax.inject.Inject

class TemplateRemoteDataSourceImpl @Inject constructor(
  private val fireStore: FirebaseFirestore
): TemplateRemoteDataSource {
  override suspend fun getFoods(): List<Food> =
    fireStore
      .collection("Food")
      .get()
      .await()
      .documents
      .map { it.toObject(Food::class.java) ?: Food() }

  override suspend fun getTemplates(
    userId: String
  ): List<Template> =
    fireStore
      .collection("Template")
      .whereEqualTo("userId", userId)
      .whereEqualTo("deletedAt", null)
//      .orderBy("createdAt", Query.Direction.DESCENDING) TODO: 쿼리상에서 정렬이 되지 않음
      .get()
      .await()
      .documents
      .map { it.toObject(Template::class.java) ?: Template() }
      .sortedByDescending { it.createdAt }

  override suspend fun addTemplate(
    template: Template
  ): Template {
    val uid = UUID.randomUUID().toString()
    val createdAt = LocalDateTime.now().toString()
    val data = template.copy(id = uid, createdAt = createdAt)

    fireStore
      .collection("Template")
      .document(uid)
      .set(data)
      .await()

    return data
  }

  override suspend fun getTemplate(
    id: String
  ): Template =
    fireStore
      .collection("Template")
      .document(id)
      .get()
      .await()
      .toObject(Template::class.java) ?: Template()

  override suspend fun editTemplate(
    template: Template
  ): Template {
    fireStore
      .collection("Template")
      .document(template.id)
      .set(template)
      .await()

    return template
  }

  override suspend fun deleteTemplate(
    id: String
  ): Unit {
    val deletedAt = LocalDateTime.now().toString()

    fireStore
      .collection("Template")
      .document(id)
      .update("deletedAt", deletedAt)
      .await()
  }
}