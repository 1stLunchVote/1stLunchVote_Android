package com.jwd.lunchvote.domain.usecase

import com.jwd.lunchvote.domain.entity.Chat
import com.jwd.lunchvote.domain.entity.Member
import com.jwd.lunchvote.domain.entity.User
import com.jwd.lunchvote.domain.repository.ChatRepository
import com.jwd.lunchvote.domain.repository.LoungeRepository
import com.jwd.lunchvote.domain.repository.MemberRepository
import java.time.Instant
import java.util.UUID
import javax.inject.Inject

class CreateLoungeUseCase @Inject constructor(
  private val loungeRepository: LoungeRepository,
  private val memberRepository: MemberRepository,
  private val chatRepository: ChatRepository
) {

  suspend operator fun invoke(user: User): String {
    val loungeId = loungeRepository.createLounge()
    loungeRepository.joinLoungeById(loungeId)

    val member = Member(
      loungeId = loungeId,
      userId = user.id,
      userName = user.name,
      userProfile = user.profileImage,
      type = Member.Type.OWNER,
      status = Member.Status.STANDBY,
      createdAt = Instant.now().epochSecond,
      deletedAt = null
    )
    memberRepository.createMember(member)

    val chat = Chat(
      id = UUID.randomUUID().toString(),
      loungeId = loungeId,
      userId = user.id,
      userName = "",
      userProfile = user.profileImage,
      message = Chat.CREATE_SYSTEM_MESSAGE,
      type = Chat.Type.SYSTEM,
      createdAt = Instant.now().epochSecond
    )
    chatRepository.sendChat(chat)

    return loungeId
  }
}