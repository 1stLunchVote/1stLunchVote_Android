package com.jwd.lunchvote.domain.usecase

import com.jwd.lunchvote.domain.entity.Chat
import com.jwd.lunchvote.domain.entity.Member
import com.jwd.lunchvote.domain.repository.ChatRepository
import com.jwd.lunchvote.domain.repository.LoungeRepository
import com.jwd.lunchvote.domain.repository.MemberRepository
import java.time.Instant
import java.util.UUID
import javax.inject.Inject

class ExileMemberUseCase @Inject constructor(
  private val loungeRepository: LoungeRepository,
  private val memberRepository: MemberRepository,
  private val chatRepository: ChatRepository
) {

  suspend operator fun invoke(member: Member) {
    loungeRepository.exitLoungeById(member.loungeId)

    memberRepository.exileMember(member)

    val chat = Chat(
      id = UUID.randomUUID().toString(),
      loungeId = member.loungeId,
      userId = member.userId,
      userName = member.userName,
      userProfile = member.userProfile,
      message = "${member.userName}님이 추방되었습니다.",
      type = Chat.Type.SYSTEM,
      createdAt = Instant.now().epochSecond
    )
    chatRepository.sendChat(chat)
  }
}