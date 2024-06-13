package com.jwd.lunchvote.presentation.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class SecondVoteFoodUIModel(
  val foodId: String = "",
  val userIds: List<String> = emptyList()
) : Parcelable
