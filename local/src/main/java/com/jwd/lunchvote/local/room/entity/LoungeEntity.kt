package com.jwd.lunchvote.local.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.jwd.lunchvote.data.model.type.LoungeStatusDataType

@Entity(tableName = "LoungeTable")
data class LoungeEntity(
  @PrimaryKey val loungeId: String,
  val status: LoungeStatusDataType
)