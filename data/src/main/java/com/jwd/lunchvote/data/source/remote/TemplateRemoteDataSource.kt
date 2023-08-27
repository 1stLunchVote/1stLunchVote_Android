package com.jwd.lunchvote.data.source.remote

import com.jwd.lunchvote.domain.entity.Food
import com.jwd.lunchvote.domain.entity.Template

interface TemplateRemoteDataSource {
  suspend fun getFoods(): List<Food>
  suspend fun getTemplates(userId: String): List<Template>
  suspend fun addTemplate(template: Template): Template
  suspend fun getTemplate(id: String): Template
  suspend fun editTemplate(template: Template): Template
}