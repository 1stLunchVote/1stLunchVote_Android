package com.jwd.lunchvote.domain.entity

data class Contact(
  val id: String,
  val userId: String,
  val title: String,
  val category: Category,
  val content: String,
  val createdAt: Long,
  val deletedAt: Long?
) {

  enum class Category {
    ACCOUNT, BUG, SUGGESTION, ETC
  }
}