package com.jwd.lunchvote.data.model

data class ContactData(
  val id: String,
  val userId: String,
  val title: String,
  val category: Category,
  val content: String,
  val createdAt: Long
) {

  enum class Category {
    ACCOUNT, BUG, SUGGESTION, ETC
  }
}
