package com.jwd.lunchvote.data.mapper

import com.jwd.lunchvote.core.common.mapper.BiMapper
import com.jwd.lunchvote.data.model.TemplateData
import com.jwd.lunchvote.domain.entity.Template

object TemplateDataMapper: BiMapper<TemplateData, Template> {
  override fun mapToRight(from: TemplateData): Template {
    return Template(
      id = from.id,
      userId = from.userId,
      name = from.name,
      like = from.like,
      dislike = from.dislike,
      createdAt = from.createdAt,
      deletedAt = from.deletedAt
    )
  }

  override fun mapToLeft(from: Template): TemplateData {
    return TemplateData(
      id = from.id,
      userId = from.userId,
      name = from.name,
      like = from.like,
      dislike = from.dislike,
      createdAt = from.createdAt,
      deletedAt = from.deletedAt
    )
  }
}

internal fun Template.asData(): TemplateData {
  return TemplateDataMapper.mapToLeft(this)
}

internal fun TemplateData.asDomain(): Template {
  return TemplateDataMapper.mapToRight(this)
}