package com.jwd.lunchvote.presentation.mapper

import com.jwd.lunchvote.mapper.BiMapper
import com.jwd.lunchvote.domain.entity.Lounge
import com.jwd.lunchvote.presentation.model.LoungeUIModel

private object LoungeUIMapper : BiMapper<LoungeUIModel, Lounge> {
  override fun mapToRight(from: LoungeUIModel): Lounge =
    Lounge(
      id = from.id,
      status = from.status.asDomain(),
      members = from.members,
      timeLimit = from.timeLimit,
      maxMembers = from.maxMembers,
      secondVoteCandidates = from.secondVoteCandidates,
      minLikeFoods = from.minLikeFoods,
      minDislikeFoods = from.minDislikeFoods
    )

  override fun mapToLeft(from: Lounge): LoungeUIModel =
    LoungeUIModel(
      id = from.id,
      status = from.status.asUI(),
      members = from.members,
      timeLimit = from.timeLimit,
      maxMembers = from.maxMembers,
      secondVoteCandidates = from.secondVoteCandidates,
      minLikeFoods = from.minLikeFoods,
      minDislikeFoods = from.minDislikeFoods
    )
}

private object LoungeUIStatusMapper : BiMapper<LoungeUIModel.Status, Lounge.Status> {
  override fun mapToRight(from: LoungeUIModel.Status): Lounge.Status =
    when (from) {
      LoungeUIModel.Status.CREATED -> Lounge.Status.CREATED
      LoungeUIModel.Status.QUIT -> Lounge.Status.QUIT
      LoungeUIModel.Status.FIRST_VOTE -> Lounge.Status.FIRST_VOTE
      LoungeUIModel.Status.SECOND_VOTE -> Lounge.Status.SECOND_VOTE
      LoungeUIModel.Status.FINISHED -> Lounge.Status.FINISHED
    }

  override fun mapToLeft(from: Lounge.Status): LoungeUIModel.Status =
    when (from) {
      Lounge.Status.CREATED -> LoungeUIModel.Status.CREATED
      Lounge.Status.QUIT -> LoungeUIModel.Status.QUIT
      Lounge.Status.FIRST_VOTE -> LoungeUIModel.Status.FIRST_VOTE
      Lounge.Status.SECOND_VOTE -> LoungeUIModel.Status.SECOND_VOTE
      Lounge.Status.FINISHED -> LoungeUIModel.Status.FINISHED
    }
}

internal fun LoungeUIModel.asDomain(): Lounge =
  LoungeUIMapper.mapToRight(this)

internal fun Lounge.asUI(): LoungeUIModel =
  LoungeUIMapper.mapToLeft(this)

internal fun LoungeUIModel.Status.asDomain(): Lounge.Status =
  LoungeUIStatusMapper.mapToRight(this)

internal fun Lounge.Status.asUI(): LoungeUIModel.Status =
  LoungeUIStatusMapper.mapToLeft(this)