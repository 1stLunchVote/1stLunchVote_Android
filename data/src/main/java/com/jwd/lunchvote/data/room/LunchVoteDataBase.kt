package com.jwd.lunchvote.data.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.jwd.lunchvote.data.room.dao.ChatDao
import com.jwd.lunchvote.data.room.dao.LoungeDao
import com.jwd.lunchvote.data.room.dao.MemberDao
import com.jwd.lunchvote.data.room.entity.ChatEntity
import com.jwd.lunchvote.data.room.entity.LoungeEntity
import com.jwd.lunchvote.data.room.entity.MemberEntity

@Database(
    entities = [
        ChatEntity::class,
        LoungeEntity::class,
        MemberEntity::class],
    version = 1
)
abstract class LunchVoteDataBase : RoomDatabase() {
    abstract fun chatDao(): ChatDao
    abstract fun loungeDao(): LoungeDao
    abstract fun memberDao(): MemberDao
}