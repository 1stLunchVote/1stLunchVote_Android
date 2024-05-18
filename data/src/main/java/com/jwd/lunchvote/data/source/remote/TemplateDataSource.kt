package com.jwd.lunchvote.data.source.remote

import com.jwd.lunchvote.data.model.TemplateData

interface TemplateDataSource {

  suspend fun addTemplate(template: TemplateData): String
  suspend fun getTemplateById(id: String): TemplateData
  suspend fun getTemplateList(userId: String): List<TemplateData>
  suspend fun updateTemplate(template: TemplateData)
  suspend fun deleteTemplateById(id: String)
}