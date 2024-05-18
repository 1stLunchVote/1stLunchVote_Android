package com.jwd.lunchvote.data.repository

import com.jwd.lunchvote.data.mapper.asData
import com.jwd.lunchvote.data.mapper.asDomain
import com.jwd.lunchvote.data.source.remote.FoodDataSource
import com.jwd.lunchvote.data.source.remote.TemplateDataSource
import com.jwd.lunchvote.domain.entity.Template
import com.jwd.lunchvote.domain.repository.TemplateRepository
import javax.inject.Inject

class TemplateRepositoryImpl @Inject constructor(
  private val foodDataSource: FoodDataSource,
  private val templateDataSource: TemplateDataSource
): TemplateRepository {

  override suspend fun getTemplateList(userId: String): List<Template> =
    templateDataSource.getTemplateList(userId).map { it.asDomain() }

  override suspend fun addTemplate(template: Template): String =
    templateDataSource.addTemplate(template.asData())

  override suspend fun getTemplate(id: String): Template =
    templateDataSource.getTemplate(id).asDomain()

  override suspend fun editTemplate(template: Template): Template =
    templateDataSource.editTemplate(template.asData()).asDomain()

  override suspend fun deleteTemplate(id: String) =
    templateDataSource.deleteTemplate(id)
}