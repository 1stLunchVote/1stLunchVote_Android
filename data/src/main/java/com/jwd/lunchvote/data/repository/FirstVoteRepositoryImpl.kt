package com.jwd.lunchvote.data.repository

import com.jwd.lunchvote.data.mapper.asDomain
import com.jwd.lunchvote.data.source.remote.FoodDataSource
import com.jwd.lunchvote.data.source.remote.TemplateDataSource
import com.jwd.lunchvote.domain.entity.Food
import com.jwd.lunchvote.domain.entity.Template
import com.jwd.lunchvote.domain.repository.FirstVoteRepository
import javax.inject.Inject

class FirstVoteRepositoryImpl @Inject constructor(
  private val templateDataSource: TemplateDataSource
) : FirstVoteRepository {

  override suspend fun getTemplateList(userId: String): List<Template> =
    templateDataSource.getTemplateList(userId).map { it.asDomain() }
}