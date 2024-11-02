package com.jwd.lunchvote.remote.mapper

import com.jwd.lunchvote.mapper.BiMapper
import com.jwd.lunchvote.data.model.LoungeData
import com.jwd.lunchvote.remote.model.LoungeRemote
import kr.co.inbody.config.error.LoungeError

private object LoungeRemoteMapper: BiMapper<LoungeRemote, LoungeData> {
  override fun mapToRight(from: LoungeRemote): LoungeData =
    LoungeData(
      id = "",
      status = from.status.asLoungeDataStatus(),
      members = from.members,
      timeLimit = from.timeLimit,
      maxMembers = from.maxMembers,
      secondVoteCandidates = from.secondVoteCandidates,
      minLikeFoods = from.minLikeFoods,
      minDislikeFoods = from.minDislikeFoods
    )

  override fun mapToLeft(from: LoungeData): LoungeRemote =
    LoungeRemote(
      status = from.status.asRemote(),
      members = from.members,
      timeLimit = from.timeLimit,
      maxMembers = from.maxMembers,
      secondVoteCandidates = from.secondVoteCandidates,
      minLikeFoods = from.minLikeFoods,
      minDislikeFoods = from.minDislikeFoods
    )
}

private object LoungeRemoteStatusMapper : BiMapper<String, LoungeData.Status> {
  override fun mapToRight(from: String): LoungeData.Status =
    when (from) {
      LoungeRemote.STATUS_CREATED -> LoungeData.Status.CREATED
      LoungeRemote.STATUS_QUIT -> LoungeData.Status.QUIT
      LoungeRemote.STATUS_FIRST_VOTE -> LoungeData.Status.FIRST_VOTE
      LoungeRemote.STATUS_SECOND_VOTE -> LoungeData.Status.SECOND_VOTE
      LoungeRemote.STATUS_FINISHED -> LoungeData.Status.FINISHED
      else -> throw LoungeError.InvalidLoungeStatus
    }

  override fun mapToLeft(from: LoungeData.Status): String =
    when (from) {
      LoungeData.Status.CREATED -> LoungeRemote.STATUS_CREATED
      LoungeData.Status.QUIT -> LoungeRemote.STATUS_QUIT
      LoungeData.Status.FIRST_VOTE -> LoungeRemote.STATUS_FIRST_VOTE
      LoungeData.Status.SECOND_VOTE -> LoungeRemote.STATUS_SECOND_VOTE
      LoungeData.Status.FINISHED -> LoungeRemote.STATUS_FINISHED
    }
}

internal fun LoungeRemote.asData(id: String): LoungeData =
  LoungeRemoteMapper.mapToRight(this).copy(id = id)

internal fun LoungeData.asRemote(): LoungeRemote =
  LoungeRemoteMapper.mapToLeft(this)

internal fun String.asLoungeDataStatus(): LoungeData.Status =
  LoungeRemoteStatusMapper.mapToRight(this)

internal fun LoungeData.Status.asRemote(): String =
  LoungeRemoteStatusMapper.mapToLeft(this)