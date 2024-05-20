package com.jwd.lunchvote.local.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "LoungeTable")
data class LoungeEntity(
  @PrimaryKey val loungeId: String,
  val status: Status,
  val members: Int
) {

  enum class Status {
    CREATED, QUIT, STARTED, FINISHED
  }
}