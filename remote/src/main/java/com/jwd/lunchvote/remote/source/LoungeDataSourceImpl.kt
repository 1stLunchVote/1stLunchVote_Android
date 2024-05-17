package com.jwd.lunchvote.remote.source

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.values
import com.google.firebase.functions.FirebaseFunctions
import com.jwd.lunchvote.core.common.error.LoungeError
import com.jwd.lunchvote.data.model.LoungeChatData
import com.jwd.lunchvote.data.model.LoungeData
import com.jwd.lunchvote.data.model.MemberData
import com.jwd.lunchvote.data.model.UserData
import com.jwd.lunchvote.data.model.type.LoungeStatusDataType
import com.jwd.lunchvote.data.model.type.MemberStatusDataType
import com.jwd.lunchvote.data.source.remote.LoungeDataSource
import com.jwd.lunchvote.remote.mapper.asData
import com.jwd.lunchvote.remote.mapper.asRemote
import com.jwd.lunchvote.remote.mapper.type.asLoungeStatusDataType
import com.jwd.lunchvote.remote.mapper.type.asMemberStatusDataType
import com.jwd.lunchvote.remote.model.LoungeChatRemote
import com.jwd.lunchvote.remote.model.LoungeRemote
import com.jwd.lunchvote.remote.model.MemberRemote
import com.jwd.lunchvote.remote.util.getValueEventFlow
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject

class LoungeDataSourceImpl @Inject constructor(
  private val functions: FirebaseFunctions,
  private val auth: FirebaseAuth,
  private val database: FirebaseDatabase,
  private val dispatcher: CoroutineDispatcher
) : LoungeDataSource {

  companion object {
    const val LOUNGE_PATH = "Lounge"
    const val CHAT_PATH = "Chat"

    //Lounge
    const val LOUNGE_STATUS = "status"
    const val LOUNGE_STATUS_CREATED = "created"
    const val LOUNGE_STATUS_STARTED = "started"
    const val LOUNGE_STATUS_FINISHED = "finished"
    const val LOUNGE_MEMBERS = "Members"

    //Member
    const val MEMBER_LOUNGE_ID = "loungeId"
    const val MEMBER_STATUS = "status"
    const val MEMBER_STATUS_OWNER = "owner"
    const val MEMBER_STATUS_JOINED = "joined"
    const val MEMBER_STATUS_READY = "ready"
    const val MEMBER_STATUS_EXILED = "exiled"
    const val MEMBER_JOINED_AT = "joinedAt"

    // Chat
    const val CHAT_LOUNGE_ID = "loungeId"
    const val CHAT_USER_ID = "userId"
    const val CHAT_USER_PROFILE = "userProfile"
    const val CHAT_MESSAGE = "message"
    const val CHAT_MESSAGE_TYPE = "messageType"
    const val CHAT_CREATED_AT = "createdAt"
  }

  override suspend fun checkLoungeExist(
    loungeId: String
  ): Boolean = withContext(dispatcher) {
    database
      .getReference(LOUNGE_PATH)
      .child(loungeId)
      .get()
      .await()
      .exists()
  }

  override suspend fun createLounge(
    owner: UserData
  ): String = withContext(dispatcher) {
    val loungeId = UUID.randomUUID().toString()
    database
      .getReference(LOUNGE_PATH)
      .child(loungeId)
      .child(LOUNGE_STATUS)
      .setValue(LOUNGE_STATUS_CREATED)
      .await()

    val member = MemberRemote(
      userName = owner.name,
      userProfile = owner.profileImageUrl,
      loungeId = loungeId,
      status = MEMBER_STATUS_OWNER
    )
    database
      .getReference(LOUNGE_PATH)
      .child(loungeId)
      .child(LOUNGE_MEMBERS)
      .child(owner.id)
      .setValue(member)
      .await()

    loungeId
  }

  override suspend fun getLoungeById(
    id: String
  ): LoungeData = withContext(dispatcher) {
    val members = database
      .getReference(LOUNGE_PATH)
      .child(id)
      .child(LOUNGE_MEMBERS)
      .get()
      .await()
      .children
      .map {
        val memberId = it.key ?: throw Exception("TODO: getLoungeById, key is null")
        it.getValue(MemberRemote::class.java)?.asData(memberId)
          ?: throw Exception("TODO: getLoungeById, key is null")
      }

    database
      .getReference(LOUNGE_PATH)
      .child(id)
      .get()
      .await()
      .getValue(LoungeRemote::class.java)
      ?.asData(id, members) ?: throw LoungeError.NoLounge
  }

  override suspend fun joinLounge(
    user: UserData,
    loungeId: String
  ): LoungeData = withContext(dispatcher) {
    val member = MemberRemote(
      userName = user.name,
      userProfile = user.profileImageUrl,
      loungeId = loungeId,
      status = MEMBER_STATUS_JOINED
    )
    database
      .getReference(LOUNGE_PATH)
      .child(loungeId)
      .child(LOUNGE_MEMBERS)
      .child(user.id)
      .setValue(member)
      .await()

    database
      .getReference(LOUNGE_PATH)
      .child(loungeId)
      .get()
      .await()
      .getValue(LoungeRemote::class.java)
      ?.asData(loungeId, listOf(member.asData(user.id))) ?: throw LoungeError.NoLounge
  }

  override fun getLoungeStatus(
    loungeId: String
  ): Flow<LoungeStatusDataType> =
    database
      .getReference(LOUNGE_PATH)
      .child(loungeId)
      .child(LOUNGE_STATUS)
      .values<String>()
      .map { status ->
        status?.asLoungeStatusDataType() ?: throw Exception("TODO: getLoungeStatus, it is null")
      }

  override fun getMemberList(
    loungeId: String
  ): Flow<List<MemberData>> {
    return database
      .getReference(LOUNGE_PATH)
      .child(loungeId)
      .child(LOUNGE_MEMBERS)
      .getValueEventFlow<MemberRemote>()
      .map {
        it.map { (key, value) ->
          value?.asData(key) ?: throw Exception("TODO: getMemberList, value is null")
        }
      }
      .map { flow ->
        flow.filter { member -> member.status != MemberStatusDataType.EXILED }
      }
      .flowOn(dispatcher)
  }

  override fun getChatList(
    loungeId: String
  ): Flow<List<LoungeChatData>> =
    database
      .getReference(CHAT_PATH)
      .child(loungeId)
      .getValueEventFlow<LoungeChatRemote>()
      .map {
        it.map { (key, value) ->
          value?.asData(key) ?: throw Exception("TODO: getChatList, hashMap is null")
        }.sortedByDescending { chat -> chat.createdAt }
      }
      .flowOn(dispatcher)

  override suspend fun sendChat(
    chat: LoungeChatData
  ) {
    withContext(dispatcher) {
      database
        .getReference(CHAT_PATH)
        .child(chat.loungeId)
        .child(chat.id)
        .setValue(chat.asRemote())
    }
  }

  override suspend fun updateReady(
    member: MemberData
  ) {
    withContext(dispatcher) {
      if (member.status == MemberStatusDataType.OWNER) {
        database
          .getReference(LOUNGE_PATH)
          .child(member.loungeId)
          .child(LOUNGE_STATUS)
          .setValue(LOUNGE_STATUS_STARTED)
      } else {
        database
          .getReference(LOUNGE_PATH)
          .child(member.loungeId)
          .child(LOUNGE_MEMBERS)
          .child(member.userId)
          .child(MEMBER_STATUS)
          .setValue(
            when (member.status) {
              MemberStatusDataType.JOINED -> MEMBER_STATUS_READY
              MemberStatusDataType.READY -> MEMBER_STATUS_JOINED
              else -> throw Exception("TODO: updateReady, invalid member status")
            }
          )
      }
    }
  }

  override suspend fun exitLounge(
    member: MemberData
  ) {
    withContext(dispatcher) {
      database
        .getReference(LOUNGE_PATH)
        .child(member.loungeId)
        .child(LOUNGE_MEMBERS)
        .child(member.userId)
        .removeValue()
    }
  }

  override suspend fun exileMember(
    member: MemberData
  ) {
    withContext(dispatcher) {
      database
        .getReference(LOUNGE_PATH)
        .child(member.loungeId)
        .child(LOUNGE_MEMBERS)
        .child(member.userId)
        .child(MEMBER_STATUS)
        .setValue(MEMBER_STATUS_EXILED)
        .await()
    }
  }

  override fun getMemberStatus(
    member: MemberData
  ): Flow<MemberStatusDataType> =
    database
      .getReference(LOUNGE_PATH)
      .child(member.loungeId)
      .child(LOUNGE_MEMBERS)
      .child(member.userId)
      .child(MEMBER_STATUS)
      .values<String>()
      .map { status ->
        status?.asMemberStatusDataType() ?: throw Exception("TODO: getMemberStatus, it is null")
      }
      .flowOn(dispatcher)

  override suspend fun getMemberByUserId(
    userId: String,
    loungeId: String
  ): MemberData =
    database
      .getReference(LOUNGE_PATH)
      .child(loungeId)
      .child(LOUNGE_MEMBERS)
      .child(userId)
      .get()
      .await()
      .getValue(MemberRemote::class.java)
      ?.asData(userId) ?: throw LoungeError.InvalidMember
}