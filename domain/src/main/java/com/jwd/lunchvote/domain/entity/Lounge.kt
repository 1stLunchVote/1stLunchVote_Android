package com.jwd.lunchvote.domain.entity

import com.jwd.lunchvote.domain.entity.type.LoungeStatus

data class Lounge(
  val id: String,
  val status: LoungeStatus,
  val members: Int
)
