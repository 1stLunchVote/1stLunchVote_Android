package com.jwd.lunchvote.domain.entity

import com.jwd.lunchvote.domain.entity.type.MessageType
import com.jwd.lunchvote.domain.entity.type.SendStatusType

data class LoungeChat(
  val id: String,
  val loungeId: String,
  val userId: String,
  val userName: String,
  val userProfile: String,
  val message: String,
  val messageType: MessageType,
  val sendStatus: SendStatusType,
  val createdAt: String
)
