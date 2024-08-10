package com.jwd.lunchvote.remote.source

import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.values
import com.jwd.lunchvote.data.model.MemberData
import com.jwd.lunchvote.data.source.remote.MemberDataSource
import com.jwd.lunchvote.remote.mapper.asData
import com.jwd.lunchvote.remote.mapper.asMemberDataType
import com.jwd.lunchvote.remote.mapper.asRemote
import com.jwd.lunchvote.remote.model.MemberRemote
import com.jwd.lunchvote.remote.util.deleteChild
import com.jwd.lunchvote.remote.util.getValueEventFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.tasks.await
import kr.co.inbody.config.error.MemberError
import javax.inject.Inject

class MemberDataSourceImpl @Inject constructor(
  private val database: FirebaseDatabase
) : MemberDataSource {

  companion object {
    const val REFERENCE_MEMBER = "Member"

    const val MEMBER_LOUNGE_ID = "loungeId"
    const val MEMBER_USER_NAME = "userName"
    const val MEMBER_USER_PROFILE = "userProfile"
    const val MEMBER_TYPE = "type"
    const val MEMBER_STATUS = "status"
    const val MEMBER_CREATED_AT = "createdAt"
    const val MEMBER_DELETED_AT = "deletedAt"
  }

  override suspend fun createMember(
    member: MemberData
  ) {
    database
      .getReference(REFERENCE_MEMBER)
      .child(member.loungeId)
      .child(member.userId)
      .setValue(member.asRemote())
      .await()
  }

  override fun getMemberListFlow(
    loungeId: String
  ): Flow<List<MemberData>> =
    database
      .getReference(REFERENCE_MEMBER)
      .child(loungeId)
      .getValueEventFlow<MemberRemote>()
      .map {
        it.mapNotNull { (key, value) -> value?.asData(key) }
          .filter { member -> member.type !in listOf(MemberData.Type.LEAVED, MemberData.Type.EXILED) }
          .sortedBy { member -> member.createdAt }
      }

  override fun getMemberTypeFlow(
    loungeId: String,
    userId: String
  ): Flow<MemberData.Type> =
    database
      .getReference(REFERENCE_MEMBER)
      .child(loungeId)
      .child(userId)
      .child(MEMBER_TYPE)
      .values<String>()
      .mapNotNull { type -> type?.asMemberDataType() }

  override suspend fun getMemberByUserId(
    userId: String,
    loungeId: String
  ): MemberData? =
    database
      .getReference(REFERENCE_MEMBER)
      .child(loungeId)
      .child(userId)
      .get()
      .await()
      .getValue(MemberRemote::class.java)
      ?.asData(userId)

  override suspend fun updateMemberReadyType(
    member: MemberData
  ) {
    database
      .getReference(REFERENCE_MEMBER)
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

  override suspend fun updateMemberType(
    member: MemberData,
    type: MemberData.Type
  ) {
    database
      .getReference(REFERENCE_MEMBER)
      .child(member.loungeId)
      .child(member.userId)
      .child(MEMBER_TYPE)
      .setValue(type.asRemote())
      .await()
  }

  override suspend fun updateMemberStatus(
    member: MemberData,
    status: MemberData.Status
  ) {
    database
      .getReference(REFERENCE_MEMBER)
      .child(member.loungeId)
      .child(member.userId)
      .child(MEMBER_STATUS)
      .setValue(status.asRemote())
      .await()
  }

  override suspend fun updateMembersStatusByLoungeId(
    loungeId: String,
    status: MemberData.Status
  ) {
    database
      .getReference(REFERENCE_MEMBER)
      .child(loungeId)
      .getValueEventFlow<MemberRemote>()
      .map {
        it.mapNotNull { (key, value) -> value?.asData(key) }
      }
      .first()
      .forEach { updateMemberStatus(it, status) }
  }

  override suspend fun exileMember(
    member: MemberData
  ) {
    database
      .getReference(REFERENCE_MEMBER)
      .child(member.loungeId)
      .child(member.userId)
      .apply {
        child(MEMBER_TYPE)
          .setValue(MemberRemote.TYPE_EXILED)
          .await()
        deleteChild()
      }
  }

  override suspend fun deleteMember(
    member: MemberData
  ) {
    database
      .getReference(REFERENCE_MEMBER)
      .child(member.loungeId)
      .child(member.userId)
      .deleteChild()
      .await()
  }
}