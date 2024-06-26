package com.jwd.lunchvote.local.source

import com.google.firebase.auth.FirebaseAuth
import com.jwd.lunchvote.data.model.ChatData
import com.jwd.lunchvote.data.model.MemberData
import com.jwd.lunchvote.data.source.local.LoungeLocalDataSource
import com.jwd.lunchvote.local.room.dao.ChatDao
import com.jwd.lunchvote.local.room.dao.LoungeDao
import com.jwd.lunchvote.local.room.dao.MemberDao
import com.jwd.lunchvote.local.room.entity.ChatEntity
import com.jwd.lunchvote.local.room.entity.LoungeEntity
import com.jwd.lunchvote.local.room.entity.MemberEntity
import com.jwd.lunchvote.local.room.mapper.asData
import com.jwd.lunchvote.local.room.mapper.asEntity
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.time.Instant
import javax.inject.Inject

class LoungeLocalDataSourceImpl @Inject constructor(
  private val chatDao: ChatDao,
  private val loungeDao: LoungeDao,
  private val memberDao: MemberDao,
  private val auth: FirebaseAuth,
  private val dispatcher: CoroutineDispatcher
) : LoungeLocalDataSource {
  override fun getChatList(
    loungeId: String,
  ): Flow<List<ChatData>> = chatDao.getAllChat(loungeId).map { it.map(ChatEntity::asData) }

  override suspend fun putChatList(
    chatList: List<ChatData>, loungeId: String
  ) = withContext(dispatcher) {
    val lounge = LoungeEntity(
      loungeId = loungeId,
      status = LoungeEntity.Status.CREATED,
      members = 0
    )
    loungeDao.insertLounge(lounge)

    chatDao.deleteAllChat(loungeId)
    chatDao.insertAllChat(chatList.map(ChatData::asEntity))
  }

  override fun getMemberList(
    loungeId: String
  ): Flow<List<MemberData>> =
    memberDao.getAllMember(loungeId).map { it.map(MemberEntity::asData) }
      .flowOn(dispatcher)

  override suspend fun putMemberList(
    memberList: List<MemberData>, loungeId: String
  ) {
    withContext(dispatcher) {
      val lounge = LoungeEntity(
        loungeId = loungeId,
        status = LoungeEntity.Status.CREATED,
        members = memberList.size
      )
      loungeDao.insertLounge(lounge)

      memberDao.deleteAllMember(loungeId)
      memberDao.insertAllMember(memberList.map(MemberData::asEntity))
    }
  }

  override suspend fun insertChat(
    id: String, loungeId: String, content: String, type: ChatData.Type
  ) {
    withContext(dispatcher) {
      auth.currentUser?.let { user ->
        chatDao.insertChat(
          ChatEntity(
            loungeId = loungeId,
            id = id,
            userId = user.uid,
            userName = user.displayName.toString(),
            userProfile = user.photoUrl.toString(),
            message = content,
            type = ChatEntity.Type.SYSTEM,
            createdAt = Instant.now().epochSecond
          )
        )
      }
    }
  }

  override suspend fun deleteChat(
    loungeId: String
  ) = withContext(dispatcher) {
    chatDao.deleteSendingChat(loungeId)
  }

  override suspend fun updateMemberReady(
    uid: String, loungeId: String
  ) = withContext(dispatcher) {
    val type = memberDao.getMemberStatus(uid, loungeId)
    memberDao.updateMemberReady(
      uid, loungeId,
      if (type == MemberEntity.Type.READY) MemberEntity.Type.DEFAULT else MemberEntity.Type.READY
    )
  }

  override suspend fun deleteAllChat(
    loungeId: String
  ) = withContext(dispatcher) {
    chatDao.deleteAllChat(loungeId)
  }
}