package com.jwd.lunchvote.presentation.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class SecondVoteResultUIModel(
  val loungeId: String = "",
  val foodId: String = "",
  val voteRatio: Float = 0f
) : Parcelable