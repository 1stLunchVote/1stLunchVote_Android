package com.jwd.lunchvote.domain.usecase

import com.jwd.lunchvote.domain.entity.Chat
import com.jwd.lunchvote.domain.entity.Lounge
import com.jwd.lunchvote.domain.entity.Member
import com.jwd.lunchvote.domain.repository.ChatRepository
import com.jwd.lunchvote.domain.repository.LoungeRepository
import com.jwd.lunchvote.domain.repository.MemberRepository
import com.jwd.lunchvote.domain.repository.UserRepository
import com.jwd.lunchvote.domain.repository.UserStatusRepository
import javax.inject.Inject

class CreateLounge @Inject constructor(
  private val userRepository: UserRepository,
  private val loungeRepository: LoungeRepository,
  private val userStatusRepository: UserStatusRepository,
  private val memberRepository: MemberRepository,
  private val chatRepository: ChatRepository
) {

  suspend operator fun invoke(userId: String): Lounge {
    val user = userRepository.getUserById(userId)

    val loungeId = loungeRepository.createLounge()
    val lounge = loungeRepository.joinLoungeById(loungeId)

    userStatusRepository.setUserLounge(userId, loungeId)

    memberRepository.createMember(
      member = Member.Builder(loungeId, user)
        .owner()
        .build()
    )
    chatRepository.sendChat(
      chat = Chat.Builder(loungeId)
        .create()
        .build()
    )

    return lounge
  }
}