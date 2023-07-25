package com.jwd.lunchvote.domain.repository

import com.jwd.lunchvote.domain.entity.Template
import kotlinx.coroutines.flow.Flow

interface TemplateRepository {
  suspend fun getTemplates(userId: String): List<Template>
}