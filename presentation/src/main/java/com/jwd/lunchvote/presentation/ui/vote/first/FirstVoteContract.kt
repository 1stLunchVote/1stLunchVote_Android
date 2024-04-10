package com.jwd.lunchvote.presentation.ui.vote.first

import android.os.Parcelable
import com.jwd.lunchvote.core.ui.base.ViewModelContract
import com.jwd.lunchvote.presentation.model.FoodUIModel
import com.jwd.lunchvote.presentation.model.TemplateUIModel
import com.jwd.lunchvote.presentation.model.enums.FoodStatus
import kotlinx.parcelize.Parcelize

class FirstVoteContract {
  @Parcelize
  data class FirstVoteState(
    val loading: Boolean = false,
    val foodMap: Map<FoodUIModel, FoodStatus> = emptyMap(),
    val likeList: List<FoodUIModel> = emptyList(),
    val dislikeList: List<FoodUIModel> = emptyList(),
    val totalMember: Int = 0,
    val endedMember: Int = 0,
    val searchKeyword: String = "",
    val finished: Boolean = false
  ): ViewModelContract.State, Parcelable {
    override fun toParcelable(): Parcelable = this
  }

  sealed interface FirstVoteEvent: ViewModelContract.Event {
    data class StartInitialize(val loungeId: String): FirstVoteEvent
    data class OnClickFood(val food: FoodUIModel): FirstVoteEvent
    data class SetSearchKeyword(val searchKeyword: String): FirstVoteEvent
    data object OnClickFinishButton: FirstVoteEvent
    data object OnClickExitButton: FirstVoteEvent
    object OnClickDismissButton: FirstVoteEvent
  }

  sealed interface FirstVoteReduce: ViewModelContract.Reduce {
    data class UpdateLoading(val loading: Boolean): FirstVoteReduce
    data class Initialize(val state: FirstVoteState): FirstVoteReduce
    data class UpdateFoodStatus(val food: FoodUIModel): FirstVoteReduce
    data class UpdateTotalMember(val totalMember: Int): FirstVoteReduce
    data class UpdateEndedMember(val endedMember: Int): FirstVoteReduce
    data class UpdateSearchKeyword(val searchKeyword: String): FirstVoteReduce
    data class UpdateFinished(val finished: Boolean): FirstVoteReduce
  }

  sealed interface FirstVoteSideEffect: ViewModelContract.SideEffect {
    data object PopBackStack: FirstVoteSideEffect
    data object NavigateToSecondVote: FirstVoteSideEffect
  }

  sealed interface FirstVoteDialogState: ViewModelContract.DialogState {
    data class SelectTemplateDialog(val templateList: List<TemplateUIModel>, val selectTemplate: (TemplateUIModel?) -> Unit): FirstVoteDialogState
    data class VoteExitDialogState(val onClickConfirmButton: () -> Unit): FirstVoteDialogState
  }
}