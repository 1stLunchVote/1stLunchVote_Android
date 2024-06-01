package com.jwd.lunchvote.local.room.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
  tableName = "MemberTable",
  foreignKeys = [
    ForeignKey(
      entity = LoungeEntity::class,
      parentColumns = ["loungeId"],
      childColumns = ["loungeId"],
      onDelete = ForeignKey.CASCADE
    )
  ]
)

data class MemberEntity(
  val loungeId: String,
  @PrimaryKey val userId: String,
  val userName: String,
  val userProfile: String,
  val type: Type,
  val status: Status,
  val createdAt: Long,
  val deletedAt: Long?
) {

  enum class Type {
    DEFAULT, OWNER, READY, EXILED
  }

  enum class Status {
    STANDBY, VOTING, VOTED
  }
}