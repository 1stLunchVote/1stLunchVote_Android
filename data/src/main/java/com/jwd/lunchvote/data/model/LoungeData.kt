package com.jwd.lunchvote.data.model

import com.jwd.lunchvote.data.model.type.LoungeStatusDataType

data class LoungeData(
  val id: String,
  val status: LoungeStatusDataType,
  val members: List<MemberData>
)
