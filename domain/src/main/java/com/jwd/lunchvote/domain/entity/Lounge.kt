package com.jwd.lunchvote.domain.entity

data class Lounge(
  val id: String,
  val status: Status,
  val members: Int,

  val timeLimit: Int?,
  val maxMembers: Int,
  val secondVoteCandidates: Int,
  val minLikeFoods: Int?,
  val minDislikeFoods: Int?
) {

  enum class Status {
    CREATED, QUIT, FIRST_VOTE, SECOND_VOTE, FINISHED
  }
}
