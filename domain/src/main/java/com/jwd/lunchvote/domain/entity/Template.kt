package com.jwd.lunchvote.domain.entity

data class Template(
  val uid: String,
  val userId: String,
  val name: String,
  val like: List<String>,
  val dislike: List<String>
)
