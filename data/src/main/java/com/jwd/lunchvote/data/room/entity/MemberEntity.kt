package com.jwd.lunchvote.data.room.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.jwd.lunchvote.domain.entity.Member

@Entity(
    tableName = "MemberTable",
    foreignKeys = [
        ForeignKey(
            entity = LoungeEntity::class,
            parentColumns = ["loungeId"],
            childColumns = ["loungeId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class MemberEntity(
    @PrimaryKey val uid: String,
    val profileImage: String? = null,
    val nickname: String,
    val ready: Boolean = false,
    val owner: Boolean = false,
    val joinedTime: String,
    val loungeId: String
){
    fun toDomain() : Member {
        return Member(
            uid = uid,
            nickname = nickname,
            profileImage = profileImage,
            ready = ready,
            owner = owner,
            joinedTime = joinedTime
        )
    }
}