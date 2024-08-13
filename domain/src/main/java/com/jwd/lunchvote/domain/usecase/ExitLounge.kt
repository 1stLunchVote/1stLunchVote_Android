package com.jwd.lunchvote.domain.usecase

import com.jwd.lunchvote.domain.entity.Chat
import com.jwd.lunchvote.domain.entity.Member.Type.LEAVED
import com.jwd.lunchvote.domain.entity.Member.Type.OWNER
import com.jwd.lunchvote.domain.repository.ChatRepository
import com.jwd.lunchvote.domain.repository.LoungeRepository
import com.jwd.lunchvote.domain.repository.MemberRepository
import com.jwd.lunchvote.domain.repository.UserStatusRepository
import kr.co.inbody.config.error.MemberError
import javax.inject.Inject

class ExitLounge @Inject constructor(
  private val userStatusRepository: UserStatusRepository,
  private val memberRepository: MemberRepository,
  private val loungeRepository: LoungeRepository,
  private val chatRepository: ChatRepository
) {

  suspend operator fun invoke(userId: String) {
    val loungeId = userStatusRepository.getUserStatus(userId)?.loungeId ?: throw MemberError.InvalidMember
    val member = memberRepository.getMember(userId, loungeId) ?: throw MemberError.InvalidMember
    
    loungeRepository.exitLoungeById(loungeId)
    if (member.type == OWNER) loungeRepository.quitLoungeById(loungeId)

    memberRepository.updateMemberType(member, LEAVED)

    chatRepository.sendChat(
      chat = Chat.Builder(loungeId)
        .member(member)
        .exit()
        .build()
    )
  }
}