package com.jwd.lunchvote.remote.source

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.functions.FirebaseFunctions
import com.jwd.lunchvote.data.di.Dispatcher
import com.jwd.lunchvote.data.di.LunchVoteDispatcher.IO
import com.jwd.lunchvote.data.source.remote.FirstVoteDataSource
import com.jwd.lunchvote.domain.entity.Food
import com.jwd.lunchvote.domain.entity.Template
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirstVoteDataSourceImpl @Inject constructor(
  private val functions: FirebaseFunctions,
  private val auth: FirebaseAuth,
  private val db: FirebaseDatabase,
  private val fireStore: FirebaseFirestore,
  @Dispatcher(IO) private val dispatcher: CoroutineDispatcher
) : FirstVoteDataSource {
  override suspend fun getFoodList(): List<Food> =
    fireStore
      .collection("Food")
      .get()
      .await()
      .documents
      .map { it.toObject(Food::class.java) ?: Food() }

  override suspend fun getTemplateList(
    userId: String
  ): List<Template> =
    fireStore
      .collection("Template")
      .whereEqualTo("userId", userId)
      .whereEqualTo("deletedAt", null)
      .get()
      .await()
      .documents
      .map { it.toObject(Template::class.java) ?: Template() }
      .sortedByDescending { it.createdAt }
}