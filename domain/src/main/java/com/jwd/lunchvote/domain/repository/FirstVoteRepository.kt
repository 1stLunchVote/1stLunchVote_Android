package com.jwd.lunchvote.domain.repository

import com.jwd.lunchvote.domain.entity.Food
import com.jwd.lunchvote.domain.entity.Template

interface FirstVoteRepository {
  suspend fun getTemplateList(userId: String): List<Template>
}