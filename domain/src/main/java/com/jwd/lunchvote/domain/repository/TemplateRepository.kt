package com.jwd.lunchvote.domain.repository

import com.jwd.lunchvote.domain.entity.Template

interface TemplateRepository {

  suspend fun getTemplateList(userId: String): List<Template>
  suspend fun addTemplate(template: Template): String
  suspend fun getTemplate(id: String): Template
  suspend fun editTemplate(template: Template): Template
  suspend fun deleteTemplate(id: String)
}