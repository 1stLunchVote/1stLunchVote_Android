package com.jwd.lunchvote.data.repository

import com.jwd.lunchvote.data.source.remote.TemplateRemoteDataSource
import com.jwd.lunchvote.domain.entity.Template
import com.jwd.lunchvote.domain.repository.TemplateRepository
import javax.inject.Inject

class TemplateRepositoryImpl @Inject constructor(
  private val templateRemoteDataSource: TemplateRemoteDataSource
): TemplateRepository {
  override suspend fun getTemplates(userId: String): List<Template> =
    templateRemoteDataSource.getTemplates(userId)
}