package com.jwd.lunchvote.local.room.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

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
  val loungeId: String,
  @PrimaryKey val id: String,
  val userId: String?,
  val message: String,
  val type: Type,
  val createdAt: Long
) {

  enum class Type {
    DEFAULT, SYSTEM
  }
}