package com.jwd.lunchvote.domain.usecase

import com.jwd.lunchvote.domain.entity.Chat
import com.jwd.lunchvote.domain.entity.Lounge
import com.jwd.lunchvote.domain.entity.Member
import com.jwd.lunchvote.domain.entity.User
import com.jwd.lunchvote.domain.repository.ChatRepository
import com.jwd.lunchvote.domain.repository.LoungeRepository
import com.jwd.lunchvote.domain.repository.MemberRepository
import kr.co.inbody.config.error.LoungeError
import java.time.Instant
import java.util.UUID
import javax.inject.Inject

class JoinLoungeUseCase @Inject constructor(
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

    loungeRepository.joinLoungeById(loungeId)

    val member = Member(
      loungeId = loungeId,
      userId = user.id,
      userName = user.name,
      userProfile = user.profileImage,
      type = Member.Type.DEFAULT,
      status = Member.Status.STANDBY,
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
      message = Chat.JOIN_SYSTEM_MESSAGE,
      type = Chat.Type.SYSTEM,
      createdAt = Instant.now().epochSecond
    )
    chatRepository.sendChat(chat)

    return loungeRepository.getLoungeById(loungeId)
  }
}