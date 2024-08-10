package com.jwd.lunchvote.domain.usecase

import com.jwd.lunchvote.domain.entity.Chat
import com.jwd.lunchvote.domain.entity.Member
import com.jwd.lunchvote.domain.repository.ChatRepository
import com.jwd.lunchvote.domain.repository.LoungeRepository
import com.jwd.lunchvote.domain.repository.MemberRepository
import javax.inject.Inject

class ExileMember @Inject constructor(
  private val loungeRepository: LoungeRepository,
  private val memberRepository: MemberRepository,
  private val chatRepository: ChatRepository
) {

  suspend operator fun invoke(member: Member) {
    loungeRepository.exitLoungeById(member.loungeId)

    memberRepository.exileMember(member)

    chatRepository.sendChat(
      chat = Chat.builder(member.loungeId)
        .type(Chat.SystemMessageType.EXILE)
        .member(member)
        .build()
    )
  }
}