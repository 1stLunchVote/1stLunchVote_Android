package com.jwd.lunchvote.data.repository

import com.jwd.lunchvote.data.mapper.asData
import com.jwd.lunchvote.data.mapper.asDomain
import com.jwd.lunchvote.data.source.remote.TemplateDataSource
import com.jwd.lunchvote.domain.entity.Template
import com.jwd.lunchvote.domain.repository.TemplateRepository
import javax.inject.Inject

class TemplateRepositoryImpl @Inject constructor(
  private val templateDataSource: TemplateDataSource
): TemplateRepository {

  override suspend fun addTemplate(template: Template): String =
    templateDataSource.addTemplate(template.asData())

  override suspend fun getTemplateById(id: String): Template =
    templateDataSource.getTemplateById(id).asDomain()

  override suspend fun getTemplateList(userId: String): List<Template> =
    templateDataSource.getTemplateList(userId).map { it.asDomain() }

  override suspend fun updateTemplate(template: Template) {
    templateDataSource.updateTemplate(template.asData())
  }

  override suspend fun deleteTemplateById(id: String) {
    templateDataSource.deleteTemplateById(id)
  }

}