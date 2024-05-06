package com.jwd.lunchvote.domain.entity

import com.jwd.lunchvote.domain.entity.type.MemberStatusType

data class Member(
  val id: String,
  val loungeId: String,
  val status: MemberStatusType,
  val joinedAt: String
)
