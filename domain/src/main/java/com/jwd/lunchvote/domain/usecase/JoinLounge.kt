package com.jwd.lunchvote.domain.usecase

import com.jwd.lunchvote.domain.entity.Chat
import com.jwd.lunchvote.domain.entity.Lounge
import com.jwd.lunchvote.domain.entity.Member
import com.jwd.lunchvote.domain.entity.User
import com.jwd.lunchvote.domain.repository.ChatRepository
import com.jwd.lunchvote.domain.repository.LoungeRepository
import com.jwd.lunchvote.domain.repository.MemberRepository
import kr.co.inbody.config.error.LoungeError
import javax.inject.Inject

class JoinLounge @Inject constructor(
  private val loungeRepository: LoungeRepository,
  private val memberRepository: MemberRepository,
  private val chatRepository: ChatRepository
) {

  suspend operator fun invoke(user: User, loungeId: String): Lounge {
    val lounge = loungeRepository.getLoungeById(loungeId)

    when (lounge.status) {
      Lounge.Status.CREATED -> Unit
      Lounge.Status.QUIT -> throw LoungeError.LoungeQuit
      Lounge.Status.FIRST_VOTE -> throw LoungeError.LoungeStarted
      Lounge.Status.SECOND_VOTE -> throw LoungeError.LoungeStarted
      Lounge.Status.FINISHED -> throw LoungeError.LoungeFinished
    }
    if (lounge.members == 6) throw LoungeError.FullMember

    memberRepository.getMemberByUserId(user.id, loungeId)?.let { member ->
      when (member.type) {
        Member.Type.LEAVED -> {
          memberRepository.updateMemberType(member, Member.Type.DEFAULT)
          chatRepository.sendChat(
            chat = Chat.builder(loungeId)
              .type(Chat.SystemMessageType.JOIN)
              .user(user)
              .build()
          )
        }
        Member.Type.EXILED -> throw LoungeError.ExiledMember
        else -> member
      }
    } ?: run {
      memberRepository.createMember(
        member = Member.builder(loungeId)
          .user(user)
          .build()
      )
      chatRepository.sendChat(
        chat = Chat.builder(loungeId)
          .type(Chat.SystemMessageType.JOIN)
          .user(user)
          .build()
      )
    }

    loungeRepository.joinLoungeById(loungeId)

    return loungeRepository.getLoungeById(loungeId)
  }
}