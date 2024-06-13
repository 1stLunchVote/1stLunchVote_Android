package com.jwd.lunchvote.presentation.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class SecondVoteUIModel(
  val loungeId: String = "",
  val foods: List<SecondVoteFoodUIModel> = emptyList()
) : Parcelable