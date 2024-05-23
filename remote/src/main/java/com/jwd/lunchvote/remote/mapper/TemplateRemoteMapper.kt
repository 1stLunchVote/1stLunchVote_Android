package com.jwd.lunchvote.remote.mapper

import com.jwd.lunchvote.core.common.mapper.BiMapper
import com.jwd.lunchvote.data.model.TemplateData
import com.jwd.lunchvote.remote.model.TemplateRemote
import com.jwd.lunchvote.remote.util.toLong
import com.jwd.lunchvote.remote.util.toTimestamp

private object TemplateRemoteMapper : BiMapper<TemplateRemote, TemplateData> {
  override fun mapToRight(from: TemplateRemote): TemplateData =
    TemplateData(
      id = "",
      userId = from.userId,
      name = from.name,
      likedFoodIds = from.likedFoodIds,
      dislikedFoodIds = from.dislikedFoodIds,
      createdAt = from.createdAt.toLong(),
      deletedAt = from.deletedAt?.toLong()
    )

  override fun mapToLeft(from: TemplateData): TemplateRemote =
    TemplateRemote(
      userId = from.userId,
      name = from.name,
      likedFoodIds = from.likedFoodIds,
      dislikedFoodIds = from.dislikedFoodIds,
      createdAt = from.createdAt.toTimestamp(),
      deletedAt = from.deletedAt?.toTimestamp()
    )
}

internal fun TemplateRemote.asData(id: String): TemplateData =
  TemplateRemoteMapper.mapToRight(this).copy(id = id)

internal fun TemplateData.asRemote(): TemplateRemote =
  TemplateRemoteMapper.mapToLeft(this)