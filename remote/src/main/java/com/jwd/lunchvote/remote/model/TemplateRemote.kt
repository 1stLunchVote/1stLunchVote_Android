package com.jwd.lunchvote.remote.model

import java.time.LocalDateTime

data class TemplateRemote(
  val userId: String = "",
  val name: String = "",
  val like: List<String> = emptyList(),
  val dislike: List<String> = emptyList(),
  val createdAt: String = LocalDateTime.now().toString(),
  val deletedAt: String? = null
)
