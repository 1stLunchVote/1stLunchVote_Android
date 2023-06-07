package com.jwd.lunchvote.data.room

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [ChatEntity::class],
    version = 1
)
abstract class LunchVoteDataBase : RoomDatabase() {
    abstract fun chatDao(): ChatDao
}