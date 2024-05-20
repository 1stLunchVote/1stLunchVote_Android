package com.jwd.lunchvote.presentation.mapper

import com.jwd.lunchvote.core.common.mapper.BiMapper
import com.jwd.lunchvote.domain.entity.Template
import com.jwd.lunchvote.presentation.model.TemplateUIModel
import com.jwd.lunchvote.presentation.util.toLocalDateTime
import com.jwd.lunchvote.presentation.util.toLong

object TemplateUIMapper: BiMapper<TemplateUIModel, Template> {
  override fun mapToRight(from: TemplateUIModel): Template =
    Template(
      id = from.id,
      userId = from.userId,
      name = from.name,
      likedFoodIds = from.likedFoodIds,
      dislikedFoodIds = from.dislikedFoodIds,
      createdAt = from.createdAt.toLong(),
      deletedAt = from.deletedAt?.toLong()
    )

  override fun mapToLeft(from: Template): TemplateUIModel =
    TemplateUIModel(
      id = from.id,
      userId = from.userId,
      name = from.name,
      likedFoodIds = from.likedFoodIds,
      dislikedFoodIds = from.dislikedFoodIds,
      createdAt = from.createdAt.toLocalDateTime(),
      deletedAt = from.deletedAt?.toLocalDateTime()
    )
}

internal fun TemplateUIModel.asDomain(): Template =
  TemplateUIMapper.mapToRight(this)

internal fun Template.asUI(): TemplateUIModel =
  TemplateUIMapper.mapToLeft(this)