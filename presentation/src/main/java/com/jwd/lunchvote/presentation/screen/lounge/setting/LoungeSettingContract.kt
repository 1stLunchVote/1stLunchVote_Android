package com.jwd.lunchvote.presentation.screen.lounge.setting

import android.os.Parcelable
import com.jwd.lunchvote.presentation.base.ViewModelContract
import com.jwd.lunchvote.presentation.model.LoungeUIModel
import com.jwd.lunchvote.presentation.util.UiText
import kotlinx.parcelize.Parcelize

class LoungeSettingContract {
  @Parcelize
  data class LoungeSettingState(
    val lounge: LoungeUIModel = LoungeUIModel(),
    val isOwner: Boolean = false,

    val timeLimitDialogState: TimeLimitDialogState? = null,
    val maxMembersDialogState: MaxMembersDialogState? = null,
    val secondVoteCandidatesDialogState: SecondVoteCandidatesDialogState? = null,
    val minLikeFoodsDialogState: MinLikeFoodsDialogState? = null,
    val minDislikeFoodsDialogState: MinDislikeFoodsDialogState? = null
  ) : ViewModelContract.State, Parcelable {
    override fun toParcelable(): Parcelable = this
  }

  sealed interface LoungeSettingEvent : ViewModelContract.Event {
    data object ScreenInitialize: LoungeSettingEvent

    data object OnClickBackButton : LoungeSettingEvent
    data object OnClickTimeLimitItem : LoungeSettingEvent
    data object OnClickMaxMembersItem : LoungeSettingEvent
    data object OnClickSecondVoteCandidatesItem : LoungeSettingEvent
    data object OnClickMinLikeFoodsItem : LoungeSettingEvent
    data object OnClickMinDislikeFoodsItem : LoungeSettingEvent
  }

  sealed interface LoungeSettingReduce : ViewModelContract.Reduce {
    data class UpdateLounge(val lounge: LoungeUIModel) : LoungeSettingReduce
    data class UpdateIsOwner(val isOwner: Boolean) : LoungeSettingReduce
    data class UpdateTimeLimitDialogState(val timeLimitDialogState: TimeLimitDialogState?) : LoungeSettingReduce
    data class UpdateMaxMembersDialogState(val maxMembersDialogState: MaxMembersDialogState?) : LoungeSettingReduce
    data class UpdateSecondVoteCandidatesDialogState(val secondVoteCandidatesDialogState: SecondVoteCandidatesDialogState?) : LoungeSettingReduce
    data class UpdateMinLikeFoodsDialogState(val minLikeFoodsDialogState: MinLikeFoodsDialogState?) : LoungeSettingReduce
    data class UpdateMinDislikeFoodsDialogState(val minDislikeFoodsDialogState: MinDislikeFoodsDialogState?) : LoungeSettingReduce
  }

  sealed interface LoungeSettingSideEffect : ViewModelContract.SideEffect {
    data object PopBackStack : LoungeSettingSideEffect
    data class ShowSnackbar(val message: UiText) : LoungeSettingSideEffect
  }

  @Parcelize
  data class TimeLimitDialogState(
    val timeLimit: Int?
  ) : ViewModelContract.State, Parcelable {
    override fun toParcelable(): Parcelable = this
  }

  sealed interface TimeLimitDialogEvent : LoungeSettingEvent {
    data object OnClickDecreaseButton : TimeLimitDialogEvent
    data object OnClickIncreaseButton : TimeLimitDialogEvent
    data object OnClickCancelButton : TimeLimitDialogEvent
    data object OnClickConfirmButton : TimeLimitDialogEvent
  }

  sealed interface TimeLimitDialogReduce : LoungeSettingReduce {
    data object DecreaseTimeLimit : TimeLimitDialogReduce
    data object IncreaseTimeLimit : TimeLimitDialogReduce
  }

  @Parcelize
  data class MaxMembersDialogState(
    val maxMembers: Int
  ) : ViewModelContract.State, Parcelable {
    override fun toParcelable(): Parcelable = this
  }

  sealed interface MaxMembersDialogEvent : LoungeSettingEvent {
    data object OnClickDecreaseButton : MaxMembersDialogEvent
    data object OnClickIncreaseButton : MaxMembersDialogEvent
    data object OnClickCancelButton : MaxMembersDialogEvent
    data object OnClickConfirmButton : MaxMembersDialogEvent
  }

  sealed interface MaxMembersDialogReduce : LoungeSettingReduce {
    data object DecreaseMaxMembers : MaxMembersDialogReduce
    data object IncreaseMaxMembers : MaxMembersDialogReduce
  }

  @Parcelize
  data class SecondVoteCandidatesDialogState(
    val secondVoteCandidates: Int
  ) : ViewModelContract.State, Parcelable {
    override fun toParcelable(): Parcelable = this
  }

  sealed interface SecondVoteCandidatesDialogEvent : LoungeSettingEvent {
    data object OnClickDecreaseButton : SecondVoteCandidatesDialogEvent
    data object OnClickIncreaseButton : SecondVoteCandidatesDialogEvent
    data object OnClickCancelButton : SecondVoteCandidatesDialogEvent
    data object OnClickConfirmButton : SecondVoteCandidatesDialogEvent
  }

  sealed interface SecondVoteCandidatesDialogReduce : LoungeSettingReduce {
    data object DecreaseSecondVoteCandidates : SecondVoteCandidatesDialogReduce
    data object IncreaseSecondVoteCandidates : SecondVoteCandidatesDialogReduce
  }

  @Parcelize
  data class MinLikeFoodsDialogState(
    val minLikeFoods: Int?
  ) : ViewModelContract.State, Parcelable {
    override fun toParcelable(): Parcelable = this
  }

  sealed interface MinLikeFoodsDialogEvent : LoungeSettingEvent {
    data object OnClickDecreaseButton : MinLikeFoodsDialogEvent
    data object OnClickIncreaseButton : MinLikeFoodsDialogEvent
    data object OnClickCancelButton : MinLikeFoodsDialogEvent
    data object OnClickConfirmButton : MinLikeFoodsDialogEvent
  }

  sealed interface MinLikeFoodsDialogReduce : LoungeSettingReduce {
    data object DecreaseMinLikeFoods : MinLikeFoodsDialogReduce
    data object IncreaseMinLikeFoods : MinLikeFoodsDialogReduce
  }

  @Parcelize
  data class MinDislikeFoodsDialogState(
    val minDislikeFoods: Int?
  ) : ViewModelContract.State, Parcelable {
    override fun toParcelable(): Parcelable = this
  }

  sealed interface MinDislikeFoodsDialogEvent : LoungeSettingEvent {
    data object OnClickDecreaseButton : MinDislikeFoodsDialogEvent
    data object OnClickIncreaseButton : MinDislikeFoodsDialogEvent
    data object OnClickCancelButton : MinDislikeFoodsDialogEvent
    data object OnClickConfirmButton : MinDislikeFoodsDialogEvent
  }

  sealed interface MinDislikeFoodsDialogReduce : LoungeSettingReduce {
    data object DecreaseMinDislikeFoods : MinDislikeFoodsDialogReduce
    data object IncreaseMinDislikeFoods : MinDislikeFoodsDialogReduce
  }
}