package com.jwd.lunchvote.domain.entity

data class Template(
  val id: String,
  val userId: String,
  val name: String,
  val like: List<String>,
  val dislike: List<String>,
  val createdAt: String,
  val deletedAt: String?
)
