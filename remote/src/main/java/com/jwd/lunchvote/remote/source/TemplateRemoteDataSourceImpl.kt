package com.jwd.lunchvote.remote.source

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.getField
import com.jwd.lunchvote.data.di.Dispatcher
import com.jwd.lunchvote.data.di.LunchVoteDispatcher.*
import com.jwd.lunchvote.data.source.remote.TemplateRemoteDataSource
import com.jwd.lunchvote.domain.entity.Template
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import javax.inject.Inject

class TemplateRemoteDataSourceImpl @Inject constructor(
  private val fireStore: FirebaseFirestore
): TemplateRemoteDataSource {
  @Suppress("UNCHECKED_CAST")
  override suspend fun getTemplates(
    userId: String
  ): List<Template> {
    val result = fireStore
      .collection("Template")
      .whereEqualTo("userId", userId)
      .get()
      .await()

    val data = mutableListOf<Template>()
    for (document in result.documents) {
      data.add(
        Template(
          uid = document.id,
          userId = document["userId"] as String,
          name = document["name"] as String,
          like = document["like"] as? List<String> ?: emptyList(),
          dislike = document["dislike"] as? List<String> ?: emptyList()
        )
      )
    }

    return data
  }
}