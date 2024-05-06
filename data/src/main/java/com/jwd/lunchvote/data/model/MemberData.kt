package com.jwd.lunchvote.data.model

import com.jwd.lunchvote.data.model.type.MemberStatusDataType

data class MemberData(
  val id: String,
  val loungeId: String,
  val status: MemberStatusDataType,
  val joinedAt: String
)