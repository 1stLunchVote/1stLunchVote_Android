package com.jwd.lunchvote.data.model

import com.jwd.lunchvote.data.model.type.MemberStatusDataType

data class MemberData(
  val userId: String,
  val userProfile: String,
  val loungeId: String,
  val status: MemberStatusDataType,
  val joinedAt: String
)