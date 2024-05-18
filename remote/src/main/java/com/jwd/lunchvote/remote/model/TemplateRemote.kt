package com.jwd.lunchvote.remote.model

import com.google.firebase.Timestamp

data class TemplateRemote(
  val userId: String = "",
  val name: String = "",
  val likedFoodIds: List<String> = emptyList(),
  val dislikedFoodIds: List<String> = emptyList(),
  val createdAt: Timestamp = Timestamp.now(),
  val deletedAt: Timestamp? = null
)
