package com.jwd.lunchvote.data.repository

import com.jwd.lunchvote.data.mapper.LoungeChatDataMapper
import com.jwd.lunchvote.data.mapper.MemberDataMapper
import com.jwd.lunchvote.data.mapper.asData
import com.jwd.lunchvote.data.mapper.asDomain
import com.jwd.lunchvote.data.mapper.type.MemberStatusDataTypeMapper
import com.jwd.lunchvote.data.model.type.MessageDataType
import com.jwd.lunchvote.data.source.local.LoungeLocalDataSource
import com.jwd.lunchvote.data.source.remote.LoungeDataSource
import com.jwd.lunchvote.data.worker.SendWorkerManager
import com.jwd.lunchvote.domain.entity.LoungeChat
import com.jwd.lunchvote.domain.entity.Member
import com.jwd.lunchvote.domain.entity.User
import com.jwd.lunchvote.domain.entity.type.LoungeStatusType
import com.jwd.lunchvote.domain.entity.type.MemberStatusType
import com.jwd.lunchvote.domain.repository.LoungeRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
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
    val id = UUID.randomUUID().toString()
    val loungeId = remote.createLounge(owner.asData())
    remote.sendChat(id, loungeId, null, MessageDataType.CREATE)
    return loungeId
  }

  override suspend fun joinLounge(user: User, loungeId: String) {
    val id = UUID.randomUUID().toString()
    remote.joinLounge(user.asData(), loungeId)
    remote.sendChat(id, loungeId, null, MessageDataType.JOIN)
  }

  override fun getMemberList(loungeId: String): Flow<List<Member>> {
    return local.getMemberList(loungeId)
      .map { list -> list.map { member -> member.asDomain() } }
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
      .map { it.map(LoungeChatDataMapper::mapToRight) }
      .filter { it.isNotEmpty() }
      .onStart { syncChatList(loungeId) }
  }

  override fun getLoungeStatus(loungeId: String): Flow<LoungeStatusType> {
    return remote.getLoungeStatus(loungeId)
      .map { LoungeStatusDataTypeMapper.mapToRight(it) }
  }

  // 일반 채팅 메시지 보내는 경우
  override suspend fun sendChat(loungeId: String, content: String) {
    val id = UUID.randomUUID().toString()
    sendWorkerManager.startSendWork(id, loungeId, content)
    return local.insertChat(id, loungeId, content, MessageDataType.NORMAL)
  }

  override suspend fun updateReady(uid: String, loungeId: String, isOwner: Boolean) {
    local.updateMemberReady(uid, loungeId)
    remote.updateReady(uid, loungeId, isOwner)
  }

  override suspend fun exitLounge(uid: String, loungeId: String) {
    val id = UUID.randomUUID().toString()
    remote.sendChat(id, loungeId, null, MessageDataType.EXIT)
    remote.exitLounge(uid, loungeId)
    local.deleteAllChat(loungeId)
  }

  override suspend fun exileMember(memberId: String, loungeId: String) {
    remote.exileMember(memberId, loungeId)
  }

  override fun getMemberStatus(uid: String, loungeId: String): Flow<MemberStatusType> {
    return remote.getMemberList(loungeId)
      .map {
        if (it.isEmpty()) {
          MemberStatusType.EXITED
        } else it.find { member -> member.id == uid }?.status?.let(MemberStatusDataTypeMapper::mapToRight)
          ?: MemberStatusType.EXILED
      }
  }

  private suspend fun syncChatList(loungeId: String) {
    remote.getChatList(loungeId)
      .map { it.map(LoungeChatDataMapper::mapToRight) }
      .onEach {
        local.putChatList(it.map(LoungeChatDataMapper::mapToLeft), loungeId)
      }
      .launchIn(CoroutineScope(currentCoroutineContext()))
  }
}