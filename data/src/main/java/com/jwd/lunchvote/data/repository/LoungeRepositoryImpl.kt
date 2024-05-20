package com.jwd.lunchvote.data.repository

import com.jwd.lunchvote.data.mapper.asData
import com.jwd.lunchvote.data.mapper.asDomain
import com.jwd.lunchvote.data.mapper.type.asDomain
import com.jwd.lunchvote.data.model.ChatData
import com.jwd.lunchvote.data.source.local.LoungeLocalDataSource
import com.jwd.lunchvote.data.source.remote.LoungeDataSource
import com.jwd.lunchvote.data.util.toLong
import com.jwd.lunchvote.domain.entity.Chat
import com.jwd.lunchvote.domain.entity.Lounge
import com.jwd.lunchvote.domain.entity.Member
import com.jwd.lunchvote.domain.entity.User
import com.jwd.lunchvote.domain.entity.type.LoungeStatus
import com.jwd.lunchvote.domain.repository.LoungeRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import java.time.LocalDateTime
import java.util.UUID
import javax.inject.Inject

class LoungeRepositoryImpl @Inject constructor(
  private val local: LoungeLocalDataSource,
  private val remote: LoungeDataSource
) : LoungeRepository {

  override suspend fun checkLoungeExist(loungeId: String): Boolean {
    return remote.checkLoungeExist(loungeId)
  }

  override suspend fun createLounge(owner: User): String {
    val loungeId = remote.createLounge(owner.asData())

    val chat = ChatData(
      id = UUID.randomUUID().toString(),
      loungeId = loungeId,
      userId = owner.id,
      userName = owner.name,
      userProfile = owner.profileImage,
      message = "투표 방이 생성되었습니다.",
      type = ChatData.Type.SYSTEM,
      createdAt = LocalDateTime.now().toLong()
    )
    remote.sendChat(chat)

    return loungeId
  }

  override suspend fun getLoungeById(id: String): Lounge =
    remote.getLoungeById(id).asDomain()

  override suspend fun joinLounge(user: User, loungeId: String): Lounge {
    val lounge = remote.joinLounge(user.asData(), loungeId)

    val chat = ChatData(
      id = UUID.randomUUID().toString(),
      loungeId = loungeId,
      userId = user.id,
      userName = user.name,
      userProfile = user.profileImage,
      message = "${user.name}님이 입장하였습니다.",
      type = ChatData.Type.SYSTEM,
      createdAt = LocalDateTime.now().toLong()
    )
    remote.sendChat(chat)

    return lounge.asDomain()
  }

  override fun getLoungeStatus(loungeId: String): Flow<LoungeStatus> =
    remote.getLoungeStatus(loungeId).map { it.asDomain() }

  override fun getMemberList(loungeId: String): Flow<List<Member>> =
    remote.getMemberList(loungeId).map { list -> list.map { it.asDomain() } }

  private suspend fun syncMemberList(loungeId: String) {
    val coroutineScope = CoroutineScope(currentCoroutineContext())
    remote.getMemberList(loungeId).onEach { list -> local.putMemberList(list, loungeId) }
      .launchIn(coroutineScope)
  }

  override fun getChatList(loungeId: String): Flow<List<Chat>> {
    return local.getChatList(loungeId).map { list -> list.map { it.asDomain() } }
      .filter { list -> list.isNotEmpty() }.onStart { syncChatList(loungeId) }
  }

  private suspend fun syncChatList(loungeId: String) {
    val coroutineScope = CoroutineScope(currentCoroutineContext())
    remote.getChatList(loungeId).map { list -> list.map { it.asDomain() } }
      .onEach { list -> local.putChatList(list.map { it.asData() }, loungeId) }
      .launchIn(coroutineScope)
  }

  // 일반 채팅 메시지 보내는 경우
  override suspend fun sendChat(chat: Chat) {
    remote.sendChat(chat.asData())
  }

  override suspend fun updateReady(member: Member) {
    local.updateMemberReady(member.userId, member.loungeId) // TOOD: 변경
    remote.updateReady(member.asData())
  }

  override suspend fun exitLounge(member: Member) {
    val chat = ChatData(
      id = UUID.randomUUID().toString(),
      loungeId = member.loungeId,
      userId = member.userId,
      userName = member.userName,
      userProfile = member.userProfile,
      message = "${member.userName}님이 퇴장하였습니다.",
      type = ChatData.Type.SYSTEM,
      createdAt = LocalDateTime.now().toLong()
    )
    remote.sendChat(chat)
    remote.exitLounge(member.asData())
    local.deleteAllChat(member.loungeId)
  }

  override suspend fun exileMember(member: Member) {
    val chat = ChatData(
      id = UUID.randomUUID().toString(),
      loungeId = member.loungeId,
      userId = member.userId,
      userName = member.userName,
      userProfile = member.userProfile,
      message = "${member.userName}님이 추방되었습니다.",
      type = ChatData.Type.SYSTEM,
      createdAt = LocalDateTime.now().toLong()
    )
    remote.sendChat(chat)
    remote.exileMember(member.asData())
  }

  override fun getMemberStatus(member: Member): Flow<Member.Type> =
    remote.getMemberStatus(member.asData()).map { it.asDomain() }

  override suspend fun getMemberByUserId(userId: String, loungeId: String): Member =
    remote.getMemberByUserId(userId, loungeId).asDomain()
}