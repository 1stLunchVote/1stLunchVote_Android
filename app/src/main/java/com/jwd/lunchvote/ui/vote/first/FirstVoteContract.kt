package com.jwd.lunchvote.ui.vote.first

import android.os.Parcelable
import com.jwd.lunchvote.core.ui.base.ViewModelContract
import com.jwd.lunchvote.model.FoodUIModel
import kotlinx.parcelize.Parcelize

class FirstVoteContract {
    @Parcelize
    data class FirstVoteState(
        val foodList: List<FoodUIModel> = emptyList(),
        val likeList: List<FoodUIModel> = emptyList(),
        val dislikeList: List<FoodUIModel> = emptyList(),
        val totalMember: Int = 0,
        val endedMember: Int = 0,
        val searchKeyword: String = "",
        val isFinished: Boolean = false,
        val voteExitDialogShown: Boolean = false,
    ): ViewModelContract.State, Parcelable {
        override fun toParcelable(): Parcelable = this
    }

    sealed interface FirstVoteEvent: ViewModelContract.Event {
        data class OnClickFood(val food: FoodUIModel): FirstVoteEvent
        data class SetSearchKeyword(val searchKeyword: String): FirstVoteEvent
        data object OnClickFinishButton: FirstVoteEvent
        data object OnTryExit : FirstVoteEvent
        data class OnClickExitDialog(val isExit: Boolean): FirstVoteEvent
    }

    sealed interface FirstVoteReduce: ViewModelContract.Reduce {
        data class UpdateFoodList(val foodList: List<FoodUIModel>): FirstVoteReduce
        data class UpdateFoodStatus(val food: FoodUIModel): FirstVoteReduce
        data class UpdateTotalMember(val totalMember: Int): FirstVoteReduce
        data class UpdateEndedMember(val endedMember: Int): FirstVoteReduce
        data class UpdateSearchKeyword(val searchKeyword: String): FirstVoteReduce
        data class UpdateIsFinished(val isFinished: Boolean): FirstVoteReduce
        data class UpdateVoteExitDialogShown(val voteExitDialogShown: Boolean): FirstVoteReduce
    }

    sealed interface FirstVoteSideEffect: ViewModelContract.SideEffect {
        data object PopBackStack: FirstVoteSideEffect
        data object NavigateToSecondVote: FirstVoteSideEffect
    }

    sealed interface FirstVoteDialogState: ViewModelContract.DialogState
}