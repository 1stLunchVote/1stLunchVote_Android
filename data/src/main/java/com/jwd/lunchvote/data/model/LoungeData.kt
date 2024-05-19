package com.jwd.lunchvote.data.model

import com.jwd.lunchvote.data.model.type.LoungeStatusData

data class LoungeData(
  val id: String,
  val status: LoungeStatusData,
  val members: Int
)
