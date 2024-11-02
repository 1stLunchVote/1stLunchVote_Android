package com.jwd.lunchvote.presentation.mapper

import kr.co.inbody.library.mapper.BiMapper
import com.jwd.lunchvote.domain.entity.Template
import com.jwd.lunchvote.presentation.model.TemplateUIModel
import com.jwd.lunchvote.presentation.util.toLong
import com.jwd.lunchvote.presentation.util.toZonedDateTime

private object TemplateUIMapper: BiMapper<TemplateUIModel, Template> {
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
      createdAt = from.createdAt.toZonedDateTime(),
      deletedAt = from.deletedAt?.toZonedDateTime()
    )
}

internal fun TemplateUIModel.asDomain(): Template =
  TemplateUIMapper.mapToRight(this)

internal fun Template.asUI(): TemplateUIModel =
  TemplateUIMapper.mapToLeft(this)