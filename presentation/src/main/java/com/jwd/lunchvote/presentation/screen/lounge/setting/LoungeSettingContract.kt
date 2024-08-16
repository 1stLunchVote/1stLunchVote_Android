package com.jwd.lunchvote.presentation.screen.lounge.setting

import android.os.Parcelable
import com.jwd.lunchvote.core.ui.base.ViewModelContract
import com.jwd.lunchvote.presentation.model.LoungeUIModel
import com.jwd.lunchvote.presentation.util.UiText
import kotlinx.parcelize.Parcelize

class LoungeSettingContract {
  @Parcelize
  data class LoungeSettingState(
    val lounge: LoungeUIModel = LoungeUIModel(),
    val isOwner: Boolean = false
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

    // DialogEvents
    data object OnClickCancelButtonDialog : LoungeSettingEvent
    data class OnClickConfirmButtonDialog(val value: Int?) : LoungeSettingEvent
  }

  sealed interface LoungeSettingReduce : ViewModelContract.Reduce {
    data class UpdateLounge(val lounge: LoungeUIModel) : LoungeSettingReduce
    data class UpdateIsOwner(val isOwner: Boolean) : LoungeSettingReduce
  }

  sealed interface LoungeSettingSideEffect : ViewModelContract.SideEffect {
    data object PopBackStack : LoungeSettingSideEffect
    data object OpenTimeLimitDialog : LoungeSettingSideEffect
    data object OpenMaxMembersDialog : LoungeSettingSideEffect
    data object OpenSecondVoteCandidatesDialog : LoungeSettingSideEffect
    data object OpenMinLikeFoodsDialog : LoungeSettingSideEffect
    data object OpenMinDislikeFoodsDialog : LoungeSettingSideEffect
    data object CloseDialog : LoungeSettingSideEffect
    data class ShowSnackbar(val message: UiText) : LoungeSettingSideEffect
  }

  companion object {
    const val TIME_LIMIT_DIALOG = "time_limit_dialog"
    const val MAX_MEMBERS_DIALOG = "member_count_dialog"
    const val SECOND_VOTE_CANDIDATES_DIALOG = "second_vote_candidates_dialog"
    const val MIN_LIKE_FOODS_DIALOG = "min_like_foods_dialog"
    const val MIN_DISLIKE_FOODS_DIALOG = "min_dislike_foods_dialog"
  }
}