package com.jwd.lunchvote.model.mapper

import com.jwd.lunchvote.domain.entity.LoungeChat
import com.jwd.lunchvote.domain.entity.Member
import com.jwd.lunchvote.model.ChatUIModel
import com.jwd.lunchvote.model.MemberUIModel

object LoungeMapper {
    fun mapToChat(loungeChat: LoungeChat, isMine: Boolean = false) : ChatUIModel {
        return ChatUIModel(
            loungeChat.content.orEmpty(),
            loungeChat.messageType,
            isMine,
            loungeChat.sender.orEmpty(),
            loungeChat.createdAt.orEmpty(),
            loungeChat.senderProfile,
            loungeChat.sendStatus
        )
    }

    fun mapToMember(member: Member, isMine: Boolean = false) : MemberUIModel {
        return MemberUIModel(
            member.uid.orEmpty(),
            member.nickname.orEmpty(),
            member.profileImage,
            member.ready,
            member.owner,
            isMine
        )
    }
}