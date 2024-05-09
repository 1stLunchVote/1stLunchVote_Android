package com.jwd.lunchvote.presentation.model

import android.os.Parcelable
import com.jwd.lunchvote.presentation.model.type.MemberStatusUIType
import kotlinx.parcelize.Parcelize

@Parcelize
data class MemberUIModel(
  val userId: String = "",
  val userProfile: String = "",
  val loungeId: String = "",
  val status: MemberStatusUIType = MemberStatusUIType.JOINED,
  val joinedAt: String = ""
) : Parcelable
