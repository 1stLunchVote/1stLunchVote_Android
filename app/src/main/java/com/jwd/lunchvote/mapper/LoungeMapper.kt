package com.jwd.lunchvote.mapper

import com.jwd.lunchvote.domain.entity.LoungeChat
import com.jwd.lunchvote.domain.entity.Member
import com.jwd.lunchvote.domain.entity.type.MemberStatusType
import com.jwd.lunchvote.model.ChatUIModel
import com.jwd.lunchvote.model.MemberUIModel

object LoungeMapper {
    fun mapToChat(loungeChat: LoungeChat, isMine: Boolean = false) : ChatUIModel {
        return ChatUIModel(
            loungeChat.message,
            loungeChat.messageType,
            isMine,
            loungeChat.userId,
            loungeChat.createdAt,
            loungeChat.userProfile,
            loungeChat.sendStatus
        )
    }

    fun mapToMember(member: Member, isMine: Boolean = false) : MemberUIModel {
        return MemberUIModel(
            member.id,
            member.name,
            member.profileImage,
            member.status == MemberStatusType.READY,
            member.isOwner,
            isMine
        )
    }
}