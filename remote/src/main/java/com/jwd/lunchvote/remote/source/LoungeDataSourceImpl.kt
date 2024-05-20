package com.jwd.lunchvote.remote.source

import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.values
import com.google.firebase.functions.FirebaseFunctions
import com.jwd.lunchvote.core.common.error.LoungeError
import com.jwd.lunchvote.data.model.ChatData
import com.jwd.lunchvote.data.model.LoungeData
import com.jwd.lunchvote.data.model.MemberData
import com.jwd.lunchvote.data.model.UserData
import com.jwd.lunchvote.data.model.type.LoungeStatusData
import com.jwd.lunchvote.data.source.remote.LoungeDataSource
import com.jwd.lunchvote.remote.mapper.asData
import com.jwd.lunchvote.remote.mapper.asMemberTypeData
import com.jwd.lunchvote.remote.mapper.asRemote
import com.jwd.lunchvote.remote.mapper.type.asLoungeStatusDataType
import com.jwd.lunchvote.remote.model.ChatRemote
import com.jwd.lunchvote.remote.model.LoungeRemote
import com.jwd.lunchvote.remote.model.MemberRemote
import com.jwd.lunchvote.remote.util.getValueEventFlow
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
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
    const val MEMBER_PATH = "Member"
    const val CHAT_PATH = "Chat"

    //Lounge
    const val LOUNGE_STATUS = "status"
    const val LOUNGE_STATUS_CREATED = "created"
    const val LOUNGE_STATUS_QUIT = "quit"
    const val LOUNGE_STATUS_STARTED = "started"
    const val LOUNGE_STATUS_FINISHED = "finished"
    const val LOUNGE_MEMBERS = "Members"

    //Member
    const val MEMBER_LOUNGE_ID = "loungeId"
    const val MEMBER_TYPE = "type"
    const val MEMBER_USER_NAME = "userName"
    const val MEMBER_USER_PROFILE = "userProfile"
    const val MEMBER_CREATED_AT = "createdAt"
    const val MEMBER_DELETED_AT = "deletedAt"

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
      loungeId = loungeId,
      userName = owner.name,
      userProfile = owner.profileImage,
      type = MemberRemote.TYPE_OWNER
    )
    database
      .getReference(MEMBER_PATH)
      .child(loungeId)
      .child(owner.id)
      .setValue(member)
      .await()

    loungeId
  }

  override suspend fun getLoungeById(
    id: String
  ): LoungeData = database
    .getReference(LOUNGE_PATH)
    .child(id)
    .get()
    .await()
    .getValue(LoungeRemote::class.java)
    ?.asData(id) ?: throw LoungeError.NoLounge

  override suspend fun joinLounge(
    user: UserData,
    loungeId: String
  ): LoungeData = withContext(dispatcher) {
    val member = MemberRemote(
      userName = user.name,
      userProfile = user.profileImage,
      loungeId = loungeId
    )
    database
      .getReference(MEMBER_PATH)
      .child(loungeId)
      .child(user.id)
      .setValue(member)
      .await()

    database
      .getReference(LOUNGE_PATH)
      .child(loungeId)
      .get()
      .await()
      .getValue(LoungeRemote::class.java)
      ?.asData(loungeId) ?: throw LoungeError.NoLounge
  }

  override fun getLoungeStatus(
    loungeId: String
  ): Flow<LoungeStatusData> =
    database
      .getReference(LOUNGE_PATH)
      .child(loungeId)
      .child(LOUNGE_STATUS)
      .values<String>()
      .mapNotNull { status -> status?.asLoungeStatusDataType() }

  override fun getMemberList(
    loungeId: String
  ): Flow<List<MemberData>> =
    database
      .getReference(MEMBER_PATH)
      .child(loungeId)
      .getValueEventFlow<MemberRemote>()
      .map {
        it.mapNotNull { (key, value) -> value?.asData(key) }
          .filter { member -> member.type != MemberData.Type.EXILED }
      }
      .flowOn(dispatcher)

  override fun getChatList(
    loungeId: String
  ): Flow<List<ChatData>> =
    database
      .getReference(CHAT_PATH)
      .child(loungeId)
      .getValueEventFlow<ChatRemote>()
      .map {
        it.mapNotNull { (key, value) -> value?.asData(key) }
          .sortedByDescending { chat -> chat.createdAt }
      }
      .flowOn(dispatcher)

  override suspend fun sendChat(
    chat: ChatData
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
      if (member.type == MemberData.Type.OWNER) {
        database
          .getReference(LOUNGE_PATH)
          .child(LOUNGE_STATUS)
          .setValue(LOUNGE_STATUS_STARTED)
          .await()
      } else {
        database
          .getReference(MEMBER_PATH)
          .child(member.loungeId)
          .child(member.userId)
          .child(MEMBER_TYPE)
          .setValue(
            when (member.type) {
              MemberData.Type.READY -> MemberRemote.TYPE_DEFAULT
              MemberData.Type.DEFAULT -> MemberRemote.TYPE_READY
              else -> throw Exception("TODO: updateReady, invalid member type")
            }
          )
          .await()
      }
    }
  }

  override suspend fun exitLounge(
    member: MemberData
  ) {
    withContext(dispatcher) {
      database
        .getReference(MEMBER_PATH)
        .child(member.loungeId)
        .child(member.userId)
        .removeValue()

      if (member.type == MemberData.Type.OWNER) {
        database
          .getReference(LOUNGE_PATH)
          .child(member.loungeId)
          .child(LOUNGE_STATUS)
          .setValue(LOUNGE_STATUS_QUIT)
      }
    }
  }

  override suspend fun exileMember(
    member: MemberData
  ) {
    withContext(dispatcher) {
      database
        .getReference(MEMBER_PATH)
        .child(member.loungeId)
        .child(member.userId)
        .apply {
          child(MEMBER_TYPE).setValue(MemberRemote.TYPE_EXILED).await()
          child(MEMBER_DELETED_AT).setValue(Timestamp.now()).await()
        }
    }
  }

  override fun getMemberStatus(
    member: MemberData
  ): Flow<MemberData.Type> =
    database
      .getReference(MEMBER_PATH)
      .child(member.loungeId)
      .child(member.userId)
      .child(MEMBER_TYPE)
      .values<String>()
      .mapNotNull { type -> type?.asMemberTypeData() }
      .flowOn(dispatcher)

  override suspend fun getMemberByUserId(
    userId: String,
    loungeId: String
  ): MemberData =
    database
      .getReference(MEMBER_PATH)
      .child(loungeId)
      .child(userId)
      .get()
      .await()
      .getValue(MemberRemote::class.java)
      ?.asData(userId) ?: throw LoungeError.InvalidMember
}