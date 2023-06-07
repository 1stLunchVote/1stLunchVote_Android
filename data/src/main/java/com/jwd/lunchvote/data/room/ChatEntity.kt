package com.jwd.lunchvote.data.room

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.jwd.lunchvote.domain.entity.LoungeChat

@Entity(tableName = "ChatTable")
data class ChatEntity(
    val sender: String,
    val senderProfile: String,
    val content: String,
    // messageType: 0 = 일반 메시지, 1 = 방 생성 및 참가
    val messageType: Int = 0,
    val createdAt: String
){
    @PrimaryKey(autoGenerate = true) var id: Long = 0

    fun toDomain() : LoungeChat {
        return LoungeChat(
            sender = sender,
            senderProfile = senderProfile,
            content = content,
            messageType = messageType,
            createdAt = createdAt
        )
    }
}