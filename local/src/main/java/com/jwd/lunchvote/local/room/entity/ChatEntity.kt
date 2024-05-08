package com.jwd.lunchvote.local.room.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.jwd.lunchvote.data.model.type.MessageDataType
import com.jwd.lunchvote.data.model.type.SendStatusDataType

@Entity(
  tableName = "ChatTable",
  foreignKeys = [
    ForeignKey(
      entity = LoungeEntity::class,
      parentColumns = ["loungeId"],
      childColumns = ["loungeId"],
      onDelete = ForeignKey.CASCADE
    )
  ]
)

data class ChatEntity(
  @PrimaryKey val id: String,
  val loungeId: String,
  val userId: String,
  val userName: String,
  val userProfile: String,
  val message: String,
  // messageType: 0 = 일반 메시지, 1 = 방 생성, 2 = 참가
  val messageType: MessageDataType,
  // sendStatus: 0 = 전송완료, 1 = 전송중, 2 = 전송실패
  val sendStatus: SendStatusDataType,
  val createdAt: String
)