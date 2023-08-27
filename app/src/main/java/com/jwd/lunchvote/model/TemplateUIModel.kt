package com.jwd.lunchvote.model

import android.os.Parcelable
import com.jwd.lunchvote.domain.entity.Template
import kotlinx.parcelize.Parcelize

@Parcelize
data class TemplateUIModel(
  val id: String,
  val userId: String,
  val name: String,
  val like: List<String>,
  val dislike: List<String>
): Parcelable {
  constructor(
    template: Template
  ): this(
    id = template.id,
    userId = template.userId,
    name = template.name,
    like = template.like,
    dislike = template.dislike
  )
}
