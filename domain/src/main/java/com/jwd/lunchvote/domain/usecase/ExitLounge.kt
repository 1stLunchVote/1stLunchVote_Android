package com.jwd.lunchvote.domain.usecase

import com.jwd.lunchvote.domain.entity.Chat
import com.jwd.lunchvote.domain.entity.Member
import com.jwd.lunchvote.domain.repository.ChatRepository
import com.jwd.lunchvote.domain.repository.LoungeRepository
import com.jwd.lunchvote.domain.repository.MemberRepository
import javax.inject.Inject

class ExitLounge @Inject constructor(
  private val loungeRepository: LoungeRepository,
  private val memberRepository: MemberRepository,
  private val chatRepository: ChatRepository
) {

  suspend operator fun invoke(member: Member) {
    loungeRepository.exitLoungeById(member.loungeId)
    if (member.type == Member.Type.OWNER) loungeRepository.quitLoungeById(member.loungeId)

    memberRepository.deleteMember(member)

    val chat = Chat.builder()
      .loungeId(member.loungeId)
      .member(member)
      .type(Chat.SystemMessageType.EXIT)
      .build()
    chatRepository.sendChat(chat)
  }
}