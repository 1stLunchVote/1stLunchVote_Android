package com.jwd.lunchvote.presentation.ui.vote.second

import android.os.Parcelable
import com.jwd.lunchvote.core.ui.base.ViewModelContract
import com.jwd.lunchvote.presentation.model.FoodItem
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
    val foodItemList: List<FoodItem> = emptyList(),
    val selectedFoodItem: FoodItem? = null,
    val finished: Boolean = false,
    val calculating: Boolean = false
  ) : ViewModelContract.State, Parcelable {
    override fun toParcelable(): Parcelable = this
  }

  sealed interface SecondVoteEvent : ViewModelContract.Event {
    data object ScreenInitialize : SecondVoteEvent

    data object OnClickBackButton : SecondVoteEvent
    data class OnClickFoodItem(val foodItem: FoodItem) : SecondVoteEvent
    data object OnClickFinishButton : SecondVoteEvent
    data object OnClickReVoteButton : SecondVoteEvent
    data object OnVoteFinish : SecondVoteEvent

    // DialogEvents
    data object OnClickCancelButtonInExitDialog : SecondVoteEvent
    data object OnClickConfirmButtonInExitDialog : SecondVoteEvent
  }

  sealed interface SecondVoteReduce : ViewModelContract.Reduce {
    data class UpdateUser(val user: UserUIModel) : SecondVoteReduce
    data class UpdateLounge(val lounge: LoungeUIModel) : SecondVoteReduce
    data class UpdateMemberList(val memberList: List<MemberUIModel>) : SecondVoteReduce
    data class UpdateFoodItemList(val foodItemList: List<FoodItem>) : SecondVoteReduce
    data class UpdateSelectedFoodItem(val foodItem: FoodItem?) : SecondVoteReduce
    data class UpdateFinished(val finished: Boolean) : SecondVoteReduce
    data class UpdateCalculating(val calculating: Boolean) : SecondVoteReduce
  }

  sealed interface SecondVoteSideEffect : ViewModelContract.SideEffect {
    data object PopBackStack : SecondVoteSideEffect
    data class NavigateToVoteResult(val loungeId: String) : SecondVoteSideEffect
    data class ShowSnackbar(val message: UiText) : SecondVoteSideEffect
  }

  sealed interface SecondVoteDialog {
    data object ExitDialog : SecondVoteDialog
  }
}