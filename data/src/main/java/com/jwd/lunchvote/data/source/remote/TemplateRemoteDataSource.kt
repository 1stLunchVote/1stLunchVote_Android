package com.jwd.lunchvote.data.source.remote

import com.jwd.lunchvote.domain.entity.Template
import kotlinx.coroutines.flow.Flow

interface TemplateRemoteDataSource {
  suspend fun getTemplates(userId: String): List<Template>
}