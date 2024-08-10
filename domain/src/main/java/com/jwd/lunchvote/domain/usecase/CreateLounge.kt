package com.jwd.lunchvote.domain.usecase

import com.jwd.lunchvote.domain.entity.Chat
import com.jwd.lunchvote.domain.entity.Member
import com.jwd.lunchvote.domain.entity.User
import com.jwd.lunchvote.domain.repository.ChatRepository
import com.jwd.lunchvote.domain.repository.LoungeRepository
import com.jwd.lunchvote.domain.repository.MemberRepository
import javax.inject.Inject

class CreateLounge @Inject constructor(
  private val loungeRepository: LoungeRepository,
  private val memberRepository: MemberRepository,
  private val chatRepository: ChatRepository
) {

  suspend operator fun invoke(user: User): String {
    val loungeId = loungeRepository.createLounge()

    loungeRepository.joinLoungeById(loungeId)
    memberRepository.createMember(
      member = Member.builder(loungeId)
        .user(user)
        .owner()
        .build()
    )
    chatRepository.sendChat(
      chat = Chat.builder(loungeId)
        .type(Chat.SystemMessageType.CREATE)
        .build()
    )

    return loungeId
  }
}