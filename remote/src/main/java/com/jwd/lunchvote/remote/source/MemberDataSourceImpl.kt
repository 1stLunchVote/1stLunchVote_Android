package com.jwd.lunchvote.remote.source

import com.google.firebase.Timestamp
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.values
import com.jwd.lunchvote.core.common.error.LoungeError
import com.jwd.lunchvote.data.model.MemberData
import com.jwd.lunchvote.data.source.remote.MemberDataSource
import com.jwd.lunchvote.remote.mapper.asData
import com.jwd.lunchvote.remote.mapper.asMemberDataType
import com.jwd.lunchvote.remote.mapper.asRemote
import com.jwd.lunchvote.remote.model.MemberRemote
import com.jwd.lunchvote.remote.util.getValueEventFlow
import com.jwd.lunchvote.remote.util.toLong
import com.kakao.sdk.common.KakaoSdk.type
import io.reactivex.rxjava3.internal.util.NotificationLite.getValue
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

class MemberDataSourceImpl @Inject constructor(
  private val database: FirebaseDatabase,
  private val dispatcher: CoroutineDispatcher
): MemberDataSource {

  companion object {
    const val MEMBER_PATH = "Member"

    const val MEMBER_LOUNGE_ID = "loungeId"
    const val MEMBER_TYPE = "type"
    const val MEMBER_USER_NAME = "userName"
    const val MEMBER_USER_PROFILE = "userProfile"
    const val MEMBER_CREATED_AT = "createdAt"
    const val MEMBER_DELETED_AT = "deletedAt"
  }

  override suspend fun createMember(
    member: MemberData
  ) {
    withContext(dispatcher) {
      database
        .getReference(MEMBER_PATH)
        .child(member.loungeId)
        .child(member.userId)
        .setValue(member.asRemote())
        .await()
    }
  }

  override fun getMemberListFlow(
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

  override fun getMemberTypeFlow(
    member: MemberData
  ): Flow<MemberData.Type> =
    database
      .getReference(MEMBER_PATH)
      .child(member.loungeId)
      .child(member.userId)
      .child(MEMBER_TYPE)
      .values<String>()
      .mapNotNull { type -> type?.asMemberDataType() }
      .flowOn(dispatcher)

  override suspend fun getMemberByUserId(
    userId: String,
    loungeId: String
  ): MemberData = withContext(dispatcher) {
    database
      .getReference(MEMBER_PATH)
      .child(loungeId)
      .child(userId)
      .get()
      .await()
      .getValue(MemberRemote::class.java)
      ?.asData(userId) ?: throw LoungeError.InvalidMember
  }

  override suspend fun updateMemberReadyType(
    member: MemberData
  ) {
    withContext(dispatcher) {
      database
        .getReference(MEMBER_PATH)
        .child(member.loungeId)
        .child(member.userId)
        .apply {
          val type = child(MEMBER_TYPE).values<String>().first()
          child(MEMBER_TYPE)
            .setValue(
              when (type) {
                MemberRemote.TYPE_DEFAULT -> MemberRemote.TYPE_READY
                MemberRemote.TYPE_READY -> MemberRemote.TYPE_DEFAULT
                else -> type
              }
            )
            .await()
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
          child(MEMBER_TYPE)
            .setValue(MemberRemote.TYPE_EXILED)
            .await()

          child(MEMBER_DELETED_AT)
            .setValue(Timestamp.now().toLong())
            .await()
        }
    }
  }

  override suspend fun deleteMember(
    member: MemberData
  ) {
    withContext(dispatcher) {
      database
        .getReference(MEMBER_PATH)
        .child(member.loungeId)
        .child(member.userId)
        .removeValue()
        .await()
    }
  }
}