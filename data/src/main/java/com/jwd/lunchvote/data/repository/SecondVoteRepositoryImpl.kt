package com.jwd.lunchvote.data.repository

import com.jwd.lunchvote.data.mapper.asDomain
import com.jwd.lunchvote.data.source.remote.SecondVoteDataSource
import com.jwd.lunchvote.domain.entity.SecondVote
import com.jwd.lunchvote.domain.repository.SecondVoteRepository
import javax.inject.Inject

class SecondVoteRepositoryImpl @Inject constructor(
  private val secondVoteDataSource: SecondVoteDataSource
) : SecondVoteRepository {

  override suspend fun createSecondVote(loungeId: String, foodIds: List<String>) =
    secondVoteDataSource.createSecondVote(loungeId, foodIds)

  override suspend fun getElectedFoodIdsByLoungeId(loungeId: String): List<String> =
    secondVoteDataSource.getElectedFoodIdsByLoungeId(loungeId)

  override suspend fun submitVote(loungeId: String, userId: String, foodId: String) =
    secondVoteDataSource.submitVote(loungeId, userId, foodId)

  override suspend fun getSecondVoteResult(loungeId: String): SecondVote =
    secondVoteDataSource.getSecondVoteResult(loungeId).asDomain()
}