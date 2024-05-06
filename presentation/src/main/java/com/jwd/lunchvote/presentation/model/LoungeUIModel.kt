package com.jwd.lunchvote.presentation.model

import com.jwd.lunchvote.presentation.model.type.LoungeStatusUIType

data class LoungeUIModel(
  val id: String = "",
  val status: LoungeStatusUIType = LoungeStatusUIType.CREATED,
  val members: List<MemberUIModel> = emptyList()
)
