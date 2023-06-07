package com.jwd.lunchvote.data.room

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

interface ChatDao {
    // 채팅 메시지 삽입
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertChat(chat: ChatEntity)

    // 채팅 메시지 리스트 삽입
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllChat(chatList: List<ChatEntity>)

    // 채팅 메시지 리스트 조회
    fun getAllChat(): List<ChatEntity>

    // Todo : 페이징으로 채팅 메시지 리스트 조회

    // 채팅 메시지 삭제
    @Delete
    fun deleteChat(chat: ChatEntity)

    // 채팅 메시지 전부 삭제
    @Query("DELETE FROM ChatTable")
    fun deleteAllChat()
}