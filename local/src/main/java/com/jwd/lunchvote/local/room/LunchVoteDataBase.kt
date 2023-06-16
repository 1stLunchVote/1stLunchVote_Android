package com.jwd.lunchvote.local.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.jwd.lunchvote.local.room.dao.ChatDao
import com.jwd.lunchvote.local.room.dao.LoungeDao
import com.jwd.lunchvote.local.room.dao.MemberDao
import com.jwd.lunchvote.local.room.entity.ChatEntity
import com.jwd.lunchvote.local.room.entity.LoungeEntity
import com.jwd.lunchvote.local.room.entity.MemberEntity

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