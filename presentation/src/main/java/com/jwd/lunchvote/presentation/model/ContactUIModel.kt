package com.jwd.lunchvote.presentation.model

import com.jwd.lunchvote.presentation.util.INITIAL_DATE_TIME
import java.time.ZonedDateTime

data class ContactUIModel(
  val id: String = "",
  val userId: String = "",
  val title: String = "",
  val category: Category = Category.ETC,
  val content: String = "",
  val createdAt: ZonedDateTime = INITIAL_DATE_TIME,
  val deletedAt: ZonedDateTime? = null
) {



  enum class Category {
    ACCOUNT, BUG, SUGGESTION, ETC
  }
}
