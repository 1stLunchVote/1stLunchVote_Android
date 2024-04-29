package com.jwd.lunchvote.presentation.mapper

import com.jwd.lunchvote.core.common.mapper.BiMapper
import com.jwd.lunchvote.domain.entity.Template
import com.jwd.lunchvote.presentation.model.TemplateUIModel

object TemplateUIMapper: BiMapper<TemplateUIModel, Template> {
  override fun mapToRight(from: TemplateUIModel): Template {
    return Template(
      id = from.id,
      userId = from.userId,
      name = from.name,
      like = from.like,
      dislike = from.dislike
    )
  }

  override fun mapToLeft(from: Template): TemplateUIModel {
    return TemplateUIModel(
      id = from.id,
      userId = from.userId,
      name = from.name,
      like = from.like,
      dislike = from.dislike
    )
  }
}

internal fun Template.asUI(): TemplateUIModel {
  return TemplateUIMapper.mapToLeft(this)
}

internal fun TemplateUIModel.asDomain(): Template {
  return TemplateUIMapper.mapToRight(this)
}