package com.jwd.lunchvote.presentation.model

import android.os.Parcelable
import com.jwd.lunchvote.domain.entity.type.MessageType
import com.jwd.lunchvote.domain.entity.type.SendStatusType
import kotlinx.parcelize.Parcelize

@Parcelize
data class ChatUIModel(
  val message: String,
  val messageType: MessageType,
  val isMine: Boolean,
  val sender: String,
  val createdAt: String,
  val userProfile: String,
  val sendStatus: SendStatusType
) : Parcelable