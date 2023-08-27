package com.jwd.lunchvote.data.repository

import com.jwd.lunchvote.data.source.remote.TemplateRemoteDataSource
import com.jwd.lunchvote.domain.entity.Food
import com.jwd.lunchvote.domain.entity.Template
import com.jwd.lunchvote.domain.repository.TemplateRepository
import javax.inject.Inject

class TemplateRepositoryImpl @Inject constructor(
  private val templateRemoteDataSource: TemplateRemoteDataSource
): TemplateRepository {
  override suspend fun getFoods(): List<Food> =
    templateRemoteDataSource.getFoods()

  override suspend fun getTemplates(userId: String): List<Template> =
    templateRemoteDataSource.getTemplates(userId)

  override suspend fun addTemplate(template: Template): Template =
    templateRemoteDataSource.addTemplate(template)

  override suspend fun getTemplate(id: String): Template =
    templateRemoteDataSource.getTemplate(id)

  override suspend fun editTemplate(template: Template): Template =
    templateRemoteDataSource.editTemplate(template)

  override suspend fun deleteTemplate(id: String): Unit =
    templateRemoteDataSource.deleteTemplate(id)
}