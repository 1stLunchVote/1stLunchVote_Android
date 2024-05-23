package com.jwd.lunchvote.domain.usecase

import com.jwd.lunchvote.domain.entity.Chat
import com.jwd.lunchvote.domain.entity.Lounge
import com.jwd.lunchvote.domain.entity.Member
import com.jwd.lunchvote.domain.entity.User
import com.jwd.lunchvote.domain.repository.ChatRepository
import com.jwd.lunchvote.domain.repository.LoungeRepository
import com.jwd.lunchvote.domain.repository.MemberRepository
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.util.UUID
import javax.inject.Inject

class JoinLoungeUseCase @Inject constructor(
  private val loungeRepository: LoungeRepository,
  private val memberRepository: MemberRepository,
  private val chatRepository: ChatRepository
) {

  suspend operator fun invoke(user: User, loungeId: String): Lounge {
    loungeRepository.joinLoungeById(loungeId)

    val member = Member(
      loungeId = loungeId,
      userId = user.id,
      userName = user.name,
      userProfile = user.profileImage,
      type = Member.Type.DEFAULT,
      createdAt = Instant.now().epochSecond,
      deletedAt = null
    )
    memberRepository.createMember(member)

    val chat = Chat(
      id = UUID.randomUUID().toString(),
      loungeId = loungeId,
      userId = user.id,
      userName = user.name,
      userProfile = user.profileImage,
      message = "${user.name}님이 입장하였습니다.",
      type = Chat.Type.SYSTEM,
      createdAt = Instant.now().epochSecond
    )
    chatRepository.sendChat(chat)

    return loungeRepository.getLoungeById(loungeId)
  }
}