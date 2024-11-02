package com.jwd.lunchvote.data.mapper

import com.jwd.lunchvote.mapper.BiMapper
import com.jwd.lunchvote.data.model.TemplateData
import com.jwd.lunchvote.domain.entity.Template

private object TemplateDataMapper: BiMapper<TemplateData, Template> {
  override fun mapToRight(from: TemplateData): Template =
    Template(
      id = from.id,
      userId = from.userId,
      name = from.name,
      likedFoodIds = from.likedFoodIds,
      dislikedFoodIds = from.dislikedFoodIds,
      createdAt = from.createdAt,
      deletedAt = from.deletedAt
    )

  override fun mapToLeft(from: Template): TemplateData =
    TemplateData(
      id = from.id,
      userId = from.userId,
      name = from.name,
      likedFoodIds = from.likedFoodIds,
      dislikedFoodIds = from.dislikedFoodIds,
      createdAt = from.createdAt,
      deletedAt = from.deletedAt
    )
}

internal fun TemplateData.asDomain(): Template =
  TemplateDataMapper.mapToRight(this)

internal fun Template.asData(): TemplateData =
  TemplateDataMapper.mapToLeft(this)