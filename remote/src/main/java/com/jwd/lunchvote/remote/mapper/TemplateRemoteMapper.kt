package com.jwd.lunchvote.remote.mapper

import com.jwd.lunchvote.core.common.base.BiMapper
import com.jwd.lunchvote.data.model.TemplateData
import com.jwd.lunchvote.remote.model.TemplateRemote

object TemplateRemoteMapper : BiMapper<TemplateRemote, TemplateData> {
  override fun mapToRight(from: TemplateRemote): TemplateData {
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

  override fun mapToLeft(from: TemplateData): TemplateRemote {
    return TemplateRemote(
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

internal fun TemplateData.asRemote(): TemplateRemote {
  return TemplateRemoteMapper.mapToLeft(this)
}

internal fun TemplateRemote.asData(): TemplateData {
  return TemplateRemoteMapper.mapToRight(this)
}