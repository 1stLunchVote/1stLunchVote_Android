package com.jwd.lunchvote.model

import android.os.Parcelable
import com.jwd.lunchvote.domain.entity.Template
import kotlinx.parcelize.Parcelize

@Parcelize
data class TemplateUIModel(
  val uid: String,
  val name: String,
  val like: List<String>,
  val dislike: List<String>
): Parcelable {
  companion object {
    fun toUIModel(template: Template): TemplateUIModel =
      TemplateUIModel(
        uid = template.uid,
        name = template.name,
        like = template.like,
        dislike = template.dislike
      )
  }
}
