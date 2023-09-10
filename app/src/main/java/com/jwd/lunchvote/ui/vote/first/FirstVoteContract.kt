package com.jwd.lunchvote.ui.vote.first

import android.os.Parcelable
import com.jwd.lunchvote.core.ui.base.ViewModelContract
import com.jwd.lunchvote.domain.entity.Food
import com.jwd.lunchvote.domain.entity.FoodStatus
import com.jwd.lunchvote.domain.entity.Template
import com.jwd.lunchvote.local.room.entity.FoodEntity
import com.jwd.lunchvote.model.FoodUIModel
import com.jwd.lunchvote.model.TemplateUIModel
import kotlinx.parcelize.Parcelize

class FirstVoteContract {
  @Parcelize
  data class FirstVoteState(
    val loading: Boolean = false,
    val foodList: List<FoodUIModel> = emptyList(),
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
  }

  sealed interface FirstVoteReduce: ViewModelContract.Reduce {
    data class UpdateLoading(val loading: Boolean): FirstVoteReduce
    data class Initialize(val state: FirstVoteState): FirstVoteReduce
    data class UpdateFoodList(val foodList: List<FoodUIModel>): FirstVoteReduce
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
    data class SelectTemplateDialog(val templateList: List<TemplateUIModel>, val selectTemplate: (TemplateUIModel) -> Unit): FirstVoteDialogState
    data class VoteExitDialogState(val onClickConfirmButton: () -> Unit): FirstVoteDialogState
  }
}