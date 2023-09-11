package com.jwd.lunchvote.data.source.remote

import com.jwd.lunchvote.domain.entity.Food
import com.jwd.lunchvote.domain.entity.Template

interface FirstVoteDataSource {
  suspend fun getFoodList(): List<Food>
  suspend fun getTemplateList(userId: String): List<Template>
}