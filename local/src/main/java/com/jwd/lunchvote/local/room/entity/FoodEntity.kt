package com.jwd.lunchvote.local.room.entity

import androidx.room.Entity

@Entity(
  tableName = "FoodTable",
  primaryKeys = ["id"]
)

data class FoodEntity(
  val id: Long,
  val imageUrl: String,
  val name: String,
)