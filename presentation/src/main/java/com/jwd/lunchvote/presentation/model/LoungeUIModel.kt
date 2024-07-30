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
  val secondVoteCandidates: Int = VoteConfig.DEFAULT_SECOND_VOTE_CANDIDATES,
  val minLikeFoods: Int? = null,
  val minDislikeFoods: Int? = null
): Parcelable {

  enum class Status {
    CREATED, QUIT, FIRST_VOTE, SECOND_VOTE, FINISHED
  }
}
