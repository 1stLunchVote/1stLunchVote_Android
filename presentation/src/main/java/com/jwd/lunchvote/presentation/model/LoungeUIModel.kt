package com.jwd.lunchvote.presentation.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kr.co.inbody.config.config.VoteConfig

@Parcelize
data class LoungeUIModel(
  val id: String = "",
  val status: Status = Status.CREATED,
  val members: Int = 0,

  val timeLimit: Int? = VoteConfig.DEFAULT_TIME_LIMIT,
  val maxMembers: Int = VoteConfig.DEFAULT_MAX_MEMBERS,
  val secondVoteCandidates: Int = VoteConfig.SECOND_VOTE_FOOD_COUNT,
  val minLikeFoods: Int? = VoteConfig.DEFAULT_MIN_LIKE_FOODS,
  val minDislikeFoods: Int? = VoteConfig.DEFAULT_MIN_DISLIKE_FOODS
): Parcelable {

  enum class Status {
    CREATED, QUIT, FIRST_VOTE, SECOND_VOTE, FINISHED
  }
}
