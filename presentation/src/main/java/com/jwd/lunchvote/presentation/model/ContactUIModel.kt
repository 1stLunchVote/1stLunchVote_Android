package com.jwd.lunchvote.presentation.model

import android.os.Parcelable
import com.jwd.lunchvote.presentation.util.INITIAL_DATE_TIME
import kotlinx.parcelize.Parcelize
import java.time.ZonedDateTime

@Parcelize
data class ContactUIModel(
  val id: String = "",
  val userId: String = "",
  val title: String = "",
  val category: Category = Category.ETC,
  val content: String = "",
  val createdAt: ZonedDateTime = INITIAL_DATE_TIME,
  val deletedAt: ZonedDateTime? = null
): Parcelable {

  enum class Category(val korean: String) {
    ACCOUNT("계정관리"), BUG("버그 및 신고"), SUGGESTION("건의사항"), ETC("기타")
  }
}
