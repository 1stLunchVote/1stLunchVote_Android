package com.jwd.lunchvote.local.room.entity

import androidx.room.Entity

@Entity(
    tableName = "FoodTable",
    primaryKeys = ["foodId"]
)

data class FoodEntity (
    val foodId: Long,
    val imageUrl: String,
    val name: String,
)
