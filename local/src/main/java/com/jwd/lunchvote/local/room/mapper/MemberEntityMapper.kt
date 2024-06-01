package com.jwd.lunchvote.local.room.mapper

import com.jwd.lunchvote.core.common.mapper.BiMapper
import com.jwd.lunchvote.data.model.MemberData
import com.jwd.lunchvote.local.room.entity.MemberEntity

private object MemberLocalMapper : BiMapper<MemberEntity, MemberData> {
  override fun mapToRight(from: MemberEntity): MemberData {
    return MemberData(
      loungeId = from.loungeId,
      userId = from.userId,
      userName = from.userName,
      userProfile = from.userProfile,
      type = from.type.asData(),
      status = from.status.asData(),
      createdAt = from.createdAt,
      deletedAt = from.deletedAt
    )
  }

  override fun mapToLeft(from: MemberData): MemberEntity {
    return MemberEntity(
      loungeId = from.loungeId,
      userId = from.userId,
      userName = from.userName,
      userProfile = from.userProfile,
      type = from.type.asEntity(),
      status = from.status.asEntity(),
      createdAt = from.createdAt,
      deletedAt = from.deletedAt
    )
  }
}

private object MemberTypeLocalMapper : BiMapper<MemberEntity.Type, MemberData.Type> {
  override fun mapToRight(from: MemberEntity.Type): MemberData.Type {
    return when (from) {
      MemberEntity.Type.DEFAULT -> MemberData.Type.DEFAULT
      MemberEntity.Type.OWNER -> MemberData.Type.OWNER
      MemberEntity.Type.READY -> MemberData.Type.READY
      MemberEntity.Type.EXILED -> MemberData.Type.EXILED
    }
  }

  override fun mapToLeft(from: MemberData.Type): MemberEntity.Type {
    return when (from) {
      MemberData.Type.DEFAULT -> MemberEntity.Type.DEFAULT
      MemberData.Type.OWNER -> MemberEntity.Type.OWNER
      MemberData.Type.READY -> MemberEntity.Type.READY
      MemberData.Type.EXILED -> MemberEntity.Type.EXILED
    }
  }
}

private object MemberStatusLocalMapper : BiMapper<MemberEntity.Status, MemberData.Status> {
  override fun mapToRight(from: MemberEntity.Status): MemberData.Status {
    return when (from) {
      MemberEntity.Status.STANDBY -> MemberData.Status.STANDBY
      MemberEntity.Status.VOTING -> MemberData.Status.VOTING
      MemberEntity.Status.VOTED -> MemberData.Status.VOTED
    }
  }

  override fun mapToLeft(from: MemberData.Status): MemberEntity.Status {
    return when (from) {
      MemberData.Status.STANDBY -> MemberEntity.Status.STANDBY
      MemberData.Status.VOTING -> MemberEntity.Status.VOTING
      MemberData.Status.VOTED -> MemberEntity.Status.VOTED
    }
  }
}

internal fun MemberEntity.asData(): MemberData {
  return MemberLocalMapper.mapToRight(this)
}

internal fun MemberData.asEntity(): MemberEntity {
  return MemberLocalMapper.mapToLeft(this)
}

internal fun MemberEntity.Type.asData(): MemberData.Type {
  return MemberTypeLocalMapper.mapToRight(this)
}

internal fun MemberData.Type.asEntity(): MemberEntity.Type {
  return MemberTypeLocalMapper.mapToLeft(this)
}

internal fun MemberEntity.Status.asData(): MemberData.Status {
  return MemberStatusLocalMapper.mapToRight(this)
}

internal fun MemberData.Status.asEntity(): MemberEntity.Status {
  return MemberStatusLocalMapper.mapToLeft(this)
}