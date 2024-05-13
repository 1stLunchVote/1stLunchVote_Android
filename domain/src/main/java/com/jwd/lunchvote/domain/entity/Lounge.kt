package com.jwd.lunchvote.domain.entity

import com.jwd.lunchvote.domain.entity.type.LoungeStatusType

data class Lounge(
  val id: String,
  val status: LoungeStatusType,
  val members: List<Member>
)
