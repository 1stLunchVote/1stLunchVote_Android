package com.jwd.lunchvote.data.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "LoungeTable")
data class LoungeEntity(
    @PrimaryKey val loungeId: String
)