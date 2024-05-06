package com.jwd.lunchvote.remote.source

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.getValue
import com.google.firebase.functions.FirebaseFunctions
import com.jwd.lunchvote.data.model.LoungeChatData
import com.jwd.lunchvote.data.model.MemberData
import com.jwd.lunchvote.data.model.UserData
import com.jwd.lunchvote.data.model.type.LoungeStatusDataType
import com.jwd.lunchvote.data.model.type.MemberStatusDataType
import com.jwd.lunchvote.data.model.type.MessageDataType
import com.jwd.lunchvote.data.source.remote.LoungeDataSource
import com.jwd.lunchvote.remote.mapper.LoungeChatRemoteMapper
import com.jwd.lunchvote.remote.mapper.asData
import com.jwd.lunchvote.remote.mapper.type.MessageRemoteTypeMapper
import com.jwd.lunchvote.remote.model.LoungeChatRemote
import com.jwd.lunchvote.remote.model.MemberRemote
import com.jwd.lunchvote.remote.util.getValueEventFlow
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.lang.reflect.Member
import java.time.LocalDateTime
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

    const val Chat_Create = "투표 방이 생성되었습니다."
    const val Chat_Join = "님이 입장했습니다."
    const val Chat_Exit = "님이 퇴장했습니다."
  }

  private fun reference(loungeId: String) = database.getReference("$LOUNGE_PATH/$loungeId")

  override suspend fun checkLoungeExist(
    loungeId: String
  ): Boolean = withContext(dispatcher) {
    reference(loungeId)
      .get()
      .await()
      .exists()
  }

  override suspend fun createLounge(
    owner: UserData
  ): String = withContext(dispatcher) {
    val loungeId = UUID.randomUUID().toString()
    reference(loungeId)
      .child(LOUNGE_STATUS)
      .setValue(LOUNGE_STATUS_CREATED)
      .await()

    val member = MemberRemote(
      loungeId = loungeId,
      status = MEMBER_STATUS_OWNER
    )
    reference(loungeId)
      .child(LOUNGE_MEMBERS)
      .child(owner.id)
      .setValue(member)
      .await()

    loungeId
  }

  override suspend fun joinLounge(
    user: UserData,
    loungeId: String
  ) {
    withContext(dispatcher) {
      val member = MemberRemote(
        loungeId = loungeId,
        status = MEMBER_STATUS_JOINED
      )
      reference(loungeId)
        .child(LOUNGE_MEMBERS)
        .child(user.id)
        .setValue(member)
        .await()
    }
  }

  override fun getMemberList(
    loungeId: String
  ): Flow<List<MemberData>> =
    reference(loungeId)
      .child(LOUNGE_MEMBERS)
      .getValueEventFlow<HashMap<String, MemberRemote>>()
      .map { hashMap ->
        hashMap
          .map { (key, value) -> value.asData(key) }
          .filter { it.status != MemberStatusDataType.EXILED }
          .sortedBy { it.joinedAt }
      }.flowOn(dispatcher)

  override fun getChatList(loungeId: String): Flow<List<LoungeChatData>> {
    val chatRef = database.getReference(Chat).child(loungeId)

    return chatRef.getValueEventFlow<HashMap<String, LoungeChatRemote>>()
      .map { it.values.map(LoungeChatRemoteMapper::mapToRight).sortedBy { chat -> chat.createdAt } }
      .flowOn(dispatcher)
  }

  override fun getLoungeStatus(loungeId: String): Flow<LoungeStatusDataType> {
    val roomRef = database.getReference("$Lounge/${loungeId}").child(Status)
    return roomRef.getValueEventFlow<String?>().map(LoungeStatusRemoteMapper::mapToRight)
  }

  override suspend fun sendChat(
    id: String, loungeId: String, content: String?, type: MessageDataType,
  ) {
    withContext(dispatcher) {
      val name = auth.currentUser?.displayName?.ifBlank { "익명" }
      val messageType = type.let(MessageRemoteTypeMapper::mapToLeft)
      val chatContent = content ?: when (messageType) {
        1 -> Chat_Create
        2 -> "$name $Chat_Join"
        else -> "$name $Chat_Exit"
      }

      val data = JSONObject().apply {
        put("id", id)
        put("loungeId", loungeId)
        put("userId", auth.currentUser?.uid)
        put("userProfile", auth.currentUser?.photoUrl.toString())
        put("message", chatContent)
        put("type", messageType)
        put("createdAt", LocalDateTime.now().toString())
      }

      functions.getHttpsCallable("sendChat").call(data).await()
    }
  }

  override suspend fun updateReady(
    uid: String, loungeId: String, isOwner: Boolean
  ) {
    withContext(dispatcher) {
      val roomRef = reference(loungeId)
      val statusRef = roomRef.child(Member).child(uid).child(Status)
      val cur = statusRef.get().await().getValue<String>()
      statusRef.setValue(if (cur == "joined") "ready" else "joined").await()

      if (isOwner) {
        roomRef.child(Status).setValue("started").await()
      }
    }
  }

  override suspend fun exitLounge(
    uid: String, loungeId: String
  ) {
    withContext(dispatcher) {
      val memberRef = reference(loungeId).child(Member).child(uid)
      memberRef.setValue(null).await()
    }
  }

  override suspend fun exileMember(memberId: String, loungeId: String) {
    withContext(dispatcher) {
      val statusRef =
        reference(loungeId).child(Member).child(memberId).child(Status)
      statusRef.setValue("exiled").await()
    }
  }
}