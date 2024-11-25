package com.jwd.lunchvote.presentation.screen.vote.second

import android.os.Parcelable
import com.jwd.lunchvote.presentation.base.ViewModelContract
import com.jwd.lunchvote.presentation.model.FoodUIModel
import com.jwd.lunchvote.presentation.model.LoungeUIModel
import com.jwd.lunchvote.presentation.model.MemberUIModel
import com.jwd.lunchvote.presentation.model.UserUIModel
import com.jwd.lunchvote.presentation.util.UiText
import kotlinx.parcelize.Parcelize

class SecondVoteContract {
  @Parcelize
  data class SecondVoteState(
    val user: UserUIModel = UserUIModel(),
    val lounge: LoungeUIModel = LoungeUIModel(),
    val memberList: List<MemberUIModel> = emptyList(),
    val foodList: List<FoodUIModel> = emptyList(),
    val selectedFood: FoodUIModel? = null,
    val finished: Boolean = false,
    val calculating: Boolean = false,

    val exitDialogState: ExitDialogState? = null
  ) : ViewModelContract.State, Parcelable {
    override fun toParcelable(): Parcelable = this
  }

  sealed interface SecondVoteEvent : ViewModelContract.Event {
    data object ScreenInitialize : SecondVoteEvent

    data object OnClickBackButton : SecondVoteEvent
    data class OnClickFood(val food: FoodUIModel) : SecondVoteEvent
    data object OnClickFinishButton : SecondVoteEvent
    data object OnClickReVoteButton : SecondVoteEvent
    data object OnVoteFinish : SecondVoteEvent
  }

  sealed interface SecondVoteReduce : ViewModelContract.Reduce {
    data class UpdateUser(val user: UserUIModel) : SecondVoteReduce
    data class UpdateLounge(val lounge: LoungeUIModel) : SecondVoteReduce
    data class UpdateMemberList(val memberList: List<MemberUIModel>) : SecondVoteReduce
    data class UpdateFoodList(val foodList: List<FoodUIModel>) : SecondVoteReduce
    data class UpdateSelectedFood(val food: FoodUIModel?) : SecondVoteReduce
    data class UpdateFinished(val finished: Boolean) : SecondVoteReduce
    data class UpdateCalculating(val calculating: Boolean) : SecondVoteReduce
    data class UpdateExitDialogState(val exitDialogState: ExitDialogState?) : SecondVoteReduce
  }

  sealed interface SecondVoteSideEffect : ViewModelContract.SideEffect {
    data object PopBackStack : SecondVoteSideEffect
    data class NavigateToVoteResult(val loungeId: String) : SecondVoteSideEffect
    data class ShowSnackbar(val message: UiText) : SecondVoteSideEffect
  }

  @Parcelize
  data object ExitDialogState : ViewModelContract.State, Parcelable {
    override fun toParcelable(): Parcelable = this
  }

  sealed interface ExitDialogEvent : SecondVoteEvent {
    data object OnClickCancelButton : ExitDialogEvent
    data object OnClickExitButton : ExitDialogEvent
  }
}