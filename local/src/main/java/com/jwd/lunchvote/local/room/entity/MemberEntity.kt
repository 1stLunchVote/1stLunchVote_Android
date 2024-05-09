package com.jwd.lunchvote.local.room.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.jwd.lunchvote.data.model.type.MemberStatusDataType

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
  @PrimaryKey val userId: String,
  val userProfile: String,
  val loungeId: String,
  val status: MemberStatusDataType,
  val joinedAt: String
)