package com.jwd.lunchvote.domain.usecase

import com.jwd.lunchvote.domain.entity.Chat
import com.jwd.lunchvote.domain.entity.Member
import com.jwd.lunchvote.domain.repository.ChatRepository
import com.jwd.lunchvote.domain.repository.LoungeRepository
import com.jwd.lunchvote.domain.repository.MemberRepository
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.util.UUID
import javax.inject.Inject

class ExitLoungeUseCase @Inject constructor(
  private val loungeRepository: LoungeRepository,
  private val memberRepository: MemberRepository,
  private val chatRepository: ChatRepository
) {

  suspend operator fun invoke(member: Member) {
    loungeRepository.exitLoungeById(member.loungeId)
    if (member.type == Member.Type.OWNER) loungeRepository.quitLoungeById(member.loungeId)

    memberRepository.deleteMember(member)

    val chat = Chat(
      id = UUID.randomUUID().toString(),
      loungeId = member.loungeId,
      userId = member.userId,
      userName = member.userName,
      userProfile = member.userProfile,
      message = "${member.userName}님이 퇴장하였습니다.",
      type = Chat.Type.SYSTEM,
      createdAt = ZonedDateTime.now(ZoneId.of("UTC")).toEpochSecond()
    )
    chatRepository.sendChat(chat)
  }
}