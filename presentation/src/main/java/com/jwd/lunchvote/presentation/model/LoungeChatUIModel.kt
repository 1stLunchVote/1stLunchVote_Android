package com.jwd.lunchvote.presentation.model

import android.os.Parcelable
import com.jwd.lunchvote.presentation.model.type.MessageUIType
import com.jwd.lunchvote.presentation.model.type.SendStatusUIType
import kotlinx.parcelize.Parcelize

@Parcelize
data class LoungeChatUIModel(
  val id: String = "",
  val loungeId: String = "",
  val userId: String = "",
  val userName: String = "",
  val userProfile: String = "",
  val message: String = "",
  val messageType: MessageUIType = MessageUIType.NORMAL,
  val sendStatus: SendStatusUIType = SendStatusUIType.FAIL,
  val createdAt: String = ""
) : Parcelable