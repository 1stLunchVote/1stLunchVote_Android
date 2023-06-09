package com.jwd.lunchvote.data.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.jwd.lunchvote.data.room.entity.ChatEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatDao {
    // 채팅 메시지 삽입
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertChat(chat: ChatEntity)

    // 채팅 메시지 리스트 삽입
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllChat(chatList: List<ChatEntity>)

    // 채팅 메시지 리스트 조회
    @Query("SELECT * FROM ChatTable WHERE loungeId = :loungeId")
    fun getAllChat(loungeId: String): Flow<List<ChatEntity>>

    // Todo : 페이징으로 채팅 메시지 리스트 조회

    // 전송중인 채팅 삭제
    @Query("DELETE FROM ChatTable WHERE sendStatus = 1 AND loungeId = :loungeId")
    fun deleteSendingChat(loungeId: String)

    // 채팅 메시지 전부 삭제
    @Query("DELETE FROM ChatTable WHERE loungeId = :loungeId")
    fun deleteAllChat(loungeId: String)
}