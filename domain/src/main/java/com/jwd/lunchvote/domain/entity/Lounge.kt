package com.jwd.lunchvote.domain.entity

data class Lounge(
  val id: String,
  val status: Status,
  val members: Int
) {

  enum class Status {
    CREATED, QUIT, STARTED, FINISHED
  }
}
