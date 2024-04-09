package com.jwd.lunchvote.data.source.remote

import com.jwd.lunchvote.data.model.TemplateData

interface TemplateDataSource {
  suspend fun getTemplateList(userId: String): List<TemplateData>
  suspend fun addTemplate(template: TemplateData): TemplateData
  suspend fun getTemplate(id: String): TemplateData
  suspend fun editTemplate(template: TemplateData): TemplateData
  suspend fun deleteTemplate(id: String)
}