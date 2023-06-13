package com.jwd.lunchvote.data.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.jwd.lunchvote.data.room.entity.LoungeEntity
import com.jwd.lunchvote.data.room.entity.MemberEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MemberDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllMember(memberList: List<MemberEntity>)

    @Query("SELECT * FROM MemberTable WHERE loungeId = :loungeId")
    fun getAllMember(loungeId: String): Flow<List<MemberEntity>>

    @Query("DELETE FROM MemberTable WHERE loungeId = :loungeId")
    fun deleteAllMember(loungeId: String)

    // 현재 레디값의 반대로 업데이트
    @Query("UPDATE MemberTable SET ready = NOT ready WHERE uid = :id AND loungeId = :loungeId")
    fun updateMemberReady(id: String, loungeId: String)
}