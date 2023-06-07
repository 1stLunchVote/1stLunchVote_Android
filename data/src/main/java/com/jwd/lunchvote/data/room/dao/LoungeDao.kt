package com.jwd.lunchvote.data.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.jwd.lunchvote.data.room.entity.LoungeEntity

@Dao
interface LoungeDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertLounge(lounge: LoungeEntity)

    @Query("DELETE from LoungeTable")
    fun deleteAllLounge()
}