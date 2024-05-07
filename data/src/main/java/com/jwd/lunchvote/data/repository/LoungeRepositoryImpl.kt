package com.jwd.lunchvote.data.repository

import com.jwd.lunchvote.data.mapper.asData
import com.jwd.lunchvote.data.mapper.asDomain
import com.jwd.lunchvote.data.mapper.type.asData
import com.jwd.lunchvote.data.mapper.type.asDomain
import com.jwd.lunchvote.data.source.local.LoungeLocalDataSource
import com.jwd.lunchvote.data.source.remote.LoungeDataSource
import com.jwd.lunchvote.data.worker.SendWorkerManager
import com.jwd.lunchvote.domain.entity.LoungeChat
import com.jwd.lunchvote.domain.entity.Member
import com.jwd.lunchvote.domain.entity.User
import com.jwd.lunchvote.domain.entity.type.LoungeStatusType
import com.jwd.lunchvote.domain.entity.type.MemberStatusType
import com.jwd.lunchvote.domain.entity.type.MessageType
import com.jwd.lunchvote.domain.entity.type.SendStatusType
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
  private val sendWorkerManager: SendWorkerManager,
  private val local: LoungeLocalDataSource,
  private val remote: LoungeDataSource
) : LoungeRepository {
  override suspend fun checkLoungeExist(loungeId: String): Boolean {
    return remote.checkLoungeExist(loungeId)
  }

  override suspend fun createLounge(owner: User): String {
    val loungeId = remote.createLounge(owner.asData())

    val chat = LoungeChat(
      id = UUID.randomUUID().toString(),
      loungeId = loungeId,
      userId = owner.id,
      userProfile = owner.profileImageUrl,
      message = "",
      messageType = MessageType.CREATE,
      sendStatus = SendStatusType.SENDING,
      createdAt = LocalDateTime.now().toString()
    )
    remote.sendChat(chat.asData())

    return loungeId
  }

  override suspend fun getLoungeById(id: String) =
    remote.getLoungeById(id).asDomain()

  override suspend fun joinLounge(user: User, loungeId: String) {
    remote.joinLounge(user.asData(), loungeId)

    val chat = LoungeChat(
      id = UUID.randomUUID().toString(),
      loungeId = loungeId,
      userId = user.id,
      userProfile = user.profileImageUrl,
      message = "",
      messageType = MessageType.JOIN,
      sendStatus = SendStatusType.SENDING,
      createdAt = LocalDateTime.now().toString()
    )
    remote.sendChat(chat.asData())
  }

  override fun getMemberList(loungeId: String): Flow<List<Member>> {
    return local.getMemberList(loungeId)
      .map { list -> list.map { it.asDomain() } }
      .onStart { syncMemberList(loungeId) }
  }

  private suspend fun syncMemberList(loungeId: String) {
    val coroutineScope = CoroutineScope(currentCoroutineContext())
    remote.getMemberList(loungeId)
      .onEach { list -> local.putMemberList(list, loungeId) }
      .launchIn(coroutineScope)
  }

  override fun getChatList(loungeId: String): Flow<List<LoungeChat>> {
    return local.getChatList(loungeId)
      .map { list -> list.map { it.asDomain() } }
      .filter { list -> list.isNotEmpty() }
      .onStart { syncChatList(loungeId) }
  }

  private suspend fun syncChatList(loungeId: String) {
    val coroutineScope = CoroutineScope(currentCoroutineContext())
    remote.getChatList(loungeId)
      .map { list -> list.map { it.asDomain() } }
      .onEach { list -> local.putChatList(list.map { it.asData() }, loungeId) }
      .launchIn(coroutineScope)
  }

  override fun getLoungeStatus(loungeId: String): Flow<LoungeStatusType> =
    remote.getLoungeStatus(loungeId).map { it.asDomain() }

  // 일반 채팅 메시지 보내는 경우
  override suspend fun sendChat(chat: LoungeChat) {
    sendWorkerManager.startSendWork(chat.asData())
    return local.insertChat(chat.id, chat.loungeId, chat.message, chat.messageType.asData()) // TOOD: 변경
  }

  override suspend fun updateReady(member: Member) {
    local.updateMemberReady(member.id, member.loungeId) // TOOD: 변경
    remote.updateReady(member.asData())
  }

  override suspend fun exitLounge(member: Member) {
    val chat = LoungeChat(
      id = UUID.randomUUID().toString(),
      loungeId = member.loungeId,
      userId = member.id,
      userProfile = "",
      message = "",
      messageType = MessageType.EXIT,
      sendStatus = SendStatusType.SENDING,
      createdAt = LocalDateTime.now().toString()
    )
    remote.sendChat(chat.asData())
    remote.exitLounge(member.asData())
    local.deleteAllChat(member.loungeId)
  }

  override suspend fun exileMember(member: Member) {
    remote.exileMember(member.asData())
  }

  override fun getMemberStatus(member: Member): Flow<MemberStatusType> =
    remote.getMemberStatus(member.asData()).map { it.asDomain() }
}