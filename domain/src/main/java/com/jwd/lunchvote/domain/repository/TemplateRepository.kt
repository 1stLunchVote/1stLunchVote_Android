package com.jwd.lunchvote.domain.repository

import com.jwd.lunchvote.domain.entity.Template

interface TemplateRepository {

  suspend fun addTemplate(template: Template): String
  suspend fun getTemplateById(id: String): Template
  suspend fun getTemplateList(userId: String): List<Template>
  suspend fun updateTemplate(template: Template)
  suspend fun deleteTemplateById(id: String)
}