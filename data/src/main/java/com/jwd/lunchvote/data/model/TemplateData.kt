package com.jwd.lunchvote.data.model

data class TemplateData(
  val id: String,
  val userId: String,
  val name: String,
  val likedFoodIds: List<String>,
  val dislikedFoodIds: List<String>,
  val createdAt: Long,
  val deletedAt: Long?
)
