package com.jwd.lunchvote.data.repository

import com.jwd.lunchvote.data.source.remote.FirstVoteDataSource
import com.jwd.lunchvote.domain.repository.FirstVoteRepository
import javax.inject.Inject

class FirstVoteRepositoryImpl @Inject constructor(
  private val firstVoteDataSource: FirstVoteDataSource
) : FirstVoteRepository {
  override suspend fun getFoodList() =
    firstVoteDataSource.getFoodList()

  override suspend fun getTemplateList(userId: String) =
    firstVoteDataSource.getTemplateList(userId)
}