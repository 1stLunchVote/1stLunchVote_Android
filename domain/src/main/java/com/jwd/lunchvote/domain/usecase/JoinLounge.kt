package com.jwd.lunchvote.domain.usecase

import com.jwd.lunchvote.domain.entity.Chat
import com.jwd.lunchvote.domain.entity.Lounge
import com.jwd.lunchvote.domain.entity.Lounge.Status.CREATED
import com.jwd.lunchvote.domain.entity.Lounge.Status.FINISHED
import com.jwd.lunchvote.domain.entity.Lounge.Status.FIRST_VOTE
import com.jwd.lunchvote.domain.entity.Lounge.Status.QUIT
import com.jwd.lunchvote.domain.entity.Lounge.Status.SECOND_VOTE
import com.jwd.lunchvote.domain.entity.Member
import com.jwd.lunchvote.domain.entity.Member.Type.DEFAULT
import com.jwd.lunchvote.domain.entity.Member.Type.EXILED
import com.jwd.lunchvote.domain.entity.Member.Type.LEAVED
import com.jwd.lunchvote.domain.repository.ChatRepository
import com.jwd.lunchvote.domain.repository.LoungeRepository
import com.jwd.lunchvote.domain.repository.MemberRepository
import com.jwd.lunchvote.domain.repository.UserRepository
import com.jwd.lunchvote.domain.repository.UserStatusRepository
import kr.co.inbody.config.error.LoungeError
import javax.inject.Inject

class JoinLounge @Inject constructor(
  private val userRepository: UserRepository,
  private val loungeRepository: LoungeRepository,
  private val memberRepository: MemberRepository,
  private val chatRepository: ChatRepository,
  private val userStatusRepository: UserStatusRepository
) {

  suspend operator fun invoke(userId: String, loungeId: String): Lounge {
    val user = userRepository.getUserById(userId)
    var lounge = loungeRepository.getLoungeById(loungeId)

    when (lounge.status) {
      CREATED -> Unit
      QUIT -> throw LoungeError.LoungeQuit
      FIRST_VOTE -> throw LoungeError.LoungeStarted
      SECOND_VOTE -> throw LoungeError.LoungeStarted
      FINISHED -> throw LoungeError.LoungeFinished
    }
    if (lounge.members == lounge.maxMembers) throw LoungeError.FullMember

    memberRepository.getMember(user.id, loungeId)?.let { member ->
      when (member.type) {
        LEAVED -> {
          memberRepository.updateMemberType(member, DEFAULT)
          chatRepository.sendChat(
            chat = Chat.Builder(loungeId)
              .user(user)
              .join()
              .build()
          )
        }
        EXILED -> throw LoungeError.ExiledMember
        else -> member
      }
    } ?: run {
      memberRepository.createMember(
        member = Member.Builder(loungeId, user)
          .build()
      )
      chatRepository.sendChat(
        chat = Chat.Builder(loungeId)
          .user(user)
          .join()
          .build()
      )
    }

    lounge = loungeRepository.joinLoungeById(loungeId)

    userStatusRepository.setUserLounge(userId, loungeId)

    return lounge
  }
}