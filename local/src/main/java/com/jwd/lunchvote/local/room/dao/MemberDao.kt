package com.jwd.lunchvote.local.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.jwd.lunchvote.data.model.type.MemberStatusDataType
import com.jwd.lunchvote.local.room.entity.MemberEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MemberDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllMember(memberList: List<MemberEntity>)

    @Query("SELECT * FROM MemberTable WHERE loungeId = :loungeId")
    fun getAllMember(loungeId: String): Flow<List<MemberEntity>>

    @Query("DELETE FROM MemberTable WHERE loungeId = :loungeId")
    fun deleteAllMember(loungeId: String)

    @Query("SELECT status FROM MemberTable WHERE userId = :userId AND loungeId = :loungeId")
    fun getMemberStatus(userId: String, loungeId: String): MemberStatusDataType

    // 현재 레디값의 반대로 업데이트
    @Query("UPDATE MemberTable SET status = :status WHERE userId = :userId AND loungeId = :loungeId")
    fun updateMemberReady(userId: String, loungeId: String, status: MemberStatusDataType)
}