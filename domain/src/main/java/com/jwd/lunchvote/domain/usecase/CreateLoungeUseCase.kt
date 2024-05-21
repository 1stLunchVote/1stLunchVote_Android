package com.jwd.lunchvote.domain.usecase

import com.jwd.lunchvote.domain.entity.Chat
import com.jwd.lunchvote.domain.entity.Member
import com.jwd.lunchvote.domain.entity.User
import com.jwd.lunchvote.domain.repository.ChatRepository
import com.jwd.lunchvote.domain.repository.LoungeRepository
import com.jwd.lunchvote.domain.repository.MemberRepository
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.UUID
import javax.inject.Inject

class CreateLoungeUseCase @Inject constructor(
  private val loungeRepository: LoungeRepository,
  private val memberRepository: MemberRepository,
  private val chatRepository: ChatRepository
) {

  suspend operator fun invoke(user: User): String {
    val loungeId = loungeRepository.createLounge()

    val member = Member(
      loungeId = loungeId,
      userId = user.id,
      userName = user.name,
      userProfile = user.profileImage,
      type = Member.Type.OWNER,
      createdAt = LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().epochSecond,
      deletedAt = null
    )
    memberRepository.createMember(member)

    val chat = Chat(
      id = UUID.randomUUID().toString(),
      loungeId = loungeId,
      userId = user.id,
      userName = user.name,
      userProfile = user.profileImage,
      message = "투표 방이 생성되었습니다.",
      type = Chat.Type.SYSTEM,
      createdAt = LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().epochSecond
    )
    chatRepository.sendChat(chat)

    return loungeId
  }
}