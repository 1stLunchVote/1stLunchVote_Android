package com.jwd.lunchvote.data.model

import com.jwd.lunchvote.data.model.type.MessageDataType
import com.jwd.lunchvote.data.model.type.SendStatusDataType

data class LoungeChatData(
  val id: String,
  val loungeId: String,
  val userId: String,
  val userName: String,
  val userProfile: String,
  val message: String,
  val messageType: MessageDataType,
  val sendStatus: SendStatusDataType,
  val createdAt: String
)