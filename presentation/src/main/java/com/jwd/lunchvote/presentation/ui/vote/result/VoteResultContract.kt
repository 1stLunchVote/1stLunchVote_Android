package com.jwd.lunchvote.presentation.ui.vote.result

import android.net.Uri
import android.os.Parcelable
import com.jwd.lunchvote.core.ui.base.ViewModelContract
import com.jwd.lunchvote.presentation.model.FoodUIModel
import com.jwd.lunchvote.presentation.util.UiText
import kotlinx.parcelize.Parcelize

class VoteResultContract {
  @Parcelize
  data class VoteResultState(
    val food: FoodUIModel = FoodUIModel(),
    val voteRatio: Float = 0f
  ) : ViewModelContract.State, Parcelable

  sealed interface VoteResultEvent : ViewModelContract.Event {
    data object ScreenInitialize : VoteResultEvent

    data object OnClickHomeButton : VoteResultEvent
  }

  sealed interface VoteResultReduce : ViewModelContract.Reduce {
    data class UpdateFood(val food: FoodUIModel) : VoteResultReduce
    data class UpdateVoteRatio(val voteRatio: Float) : VoteResultReduce
  }

  sealed interface VoteResultSideEffect : ViewModelContract.SideEffect {
    data object NavigateToHome : VoteResultSideEffect
    data class ShowSnackbar(val message: UiText) : VoteResultSideEffect
  }
}