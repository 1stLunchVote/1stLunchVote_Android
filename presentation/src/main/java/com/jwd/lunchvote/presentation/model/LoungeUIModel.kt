package com.jwd.lunchvote.presentation.model

import android.os.Parcelable
import com.jwd.lunchvote.presentation.model.type.LoungeStatusUIType
import kotlinx.parcelize.Parcelize

@Parcelize
data class LoungeUIModel(
  val id: String = "",
  val status: LoungeStatusUIType = LoungeStatusUIType.CREATED,
  val members: Int = 0
): Parcelable
