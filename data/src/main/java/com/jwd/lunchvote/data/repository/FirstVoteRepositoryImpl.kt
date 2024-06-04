package com.jwd.lunchvote.data.repository

import com.jwd.lunchvote.data.mapper.asDomain
import com.jwd.lunchvote.data.source.remote.FirstVoteDataSource
import com.jwd.lunchvote.domain.entity.FirstVote
import com.jwd.lunchvote.domain.repository.FirstVoteRepository
import javax.inject.Inject

class FirstVoteRepositoryImpl @Inject constructor(
  private val firstVoteDataSource: FirstVoteDataSource
) : FirstVoteRepository {
  
  override suspend fun submitVote(loungeId: String, userId: String, likedFoodIds: List<String>, dislikedFoodIds: List<String>) {
    firstVoteDataSource.submitVote(loungeId, userId, likedFoodIds, dislikedFoodIds)
  }

  override suspend fun getAllFirstVotes(loungeId: String): List<FirstVote> =
    firstVoteDataSource.getAllFirstVotes(loungeId).map { it.asDomain() }
}