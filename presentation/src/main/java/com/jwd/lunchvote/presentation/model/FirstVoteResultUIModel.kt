package com.jwd.lunchvote.presentation.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class FirstVoteResultUIModel(
  val loungeId: String = "",
  val foodIds: List<String> = emptyList()
) : Parcelable