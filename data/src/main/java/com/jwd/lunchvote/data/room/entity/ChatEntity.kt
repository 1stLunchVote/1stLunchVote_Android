package com.jwd.lunchvote.data.room.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.jwd.lunchvote.domain.entity.LoungeChat

@Entity(
    tableName = "ChatTable",
    foreignKeys = [
        ForeignKey(
            entity = LoungeEntity::class,
            parentColumns = ["loungeId"],
            childColumns = ["loungeId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    primaryKeys = ["chatId", "loungeId"]
)

data class ChatEntity(
    val chatId: Long,
    val sender: String,
    val senderProfile: String,
    val content: String,
    // messageType: 0 = 일반 메시지, 1 = 방 생성 및 참가
    val messageType: Int = 0,
    val createdAt: String,
    val loungeId: String
){
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