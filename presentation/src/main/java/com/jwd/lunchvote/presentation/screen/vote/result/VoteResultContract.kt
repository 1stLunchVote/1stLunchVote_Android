package com.jwd.lunchvote.presentation.screen.vote.result

import android.os.Parcelable
import com.jwd.lunchvote.presentation.base.ViewModelContract
import com.jwd.lunchvote.presentation.model.FoodUIModel
import com.jwd.lunchvote.presentation.util.UiText
import kotlinx.parcelize.Parcelize

class VoteResultContract {
  @Parcelize
  data class VoteResultState(
    val food: FoodUIModel = FoodUIModel(),
    val voteRatio: Float = 0f,
    val coverAlpha: Float = 1f
  ) : ViewModelContract.State, Parcelable

  sealed interface VoteResultEvent : ViewModelContract.Event {
    data object ScreenInitialize : VoteResultEvent

    data object OnPressRevealBox : VoteResultEvent
    data object OnClickHomeButton : VoteResultEvent
  }

  sealed interface VoteResultReduce : ViewModelContract.Reduce {
    data class UpdateFood(val food: FoodUIModel) : VoteResultReduce
    data class UpdateVoteRatio(val voteRatio: Float) : VoteResultReduce
    data object DecreaseCoverAlpha : VoteResultReduce
  }

  sealed interface VoteResultSideEffect : ViewModelContract.SideEffect {
    data object NavigateToHome : VoteResultSideEffect
    data class ShowSnackbar(val message: UiText) : VoteResultSideEffect
  }
}