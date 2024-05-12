package com.jwd.lunchvote.local.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.jwd.lunchvote.local.room.dao.ChatDao
import com.jwd.lunchvote.local.room.dao.FoodDao
import com.jwd.lunchvote.local.room.dao.LoungeDao
import com.jwd.lunchvote.local.room.dao.MemberDao
import com.jwd.lunchvote.local.room.entity.ChatEntity
import com.jwd.lunchvote.local.room.entity.FoodEntity
import com.jwd.lunchvote.local.room.entity.LoungeEntity
import com.jwd.lunchvote.local.room.entity.MemberEntity

@Database(
  entities = [
    ChatEntity::class,
    LoungeEntity::class,
    MemberEntity::class,
    FoodEntity::class
  ],
  version = 5,
  exportSchema = false
)

abstract class LunchVoteDataBase : RoomDatabase() {
  abstract fun chatDao(): ChatDao
  abstract fun loungeDao(): LoungeDao
  abstract fun memberDao(): MemberDao
  abstract fun foodDao(): FoodDao

  companion object {
    @Volatile
    private var INSTANCE: LunchVoteDataBase? = null

    fun getDatabase(context: Context): LunchVoteDataBase = INSTANCE ?: synchronized(this) {
      Room.databaseBuilder(context, LunchVoteDataBase::class.java, "station_database")
        .fallbackToDestructiveMigration()
        .build()
        .also { INSTANCE = it }
    }
  }
}