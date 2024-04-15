package com.jwd.lunchvote.presentation.ui.home.dialog

import android.os.Parcelable
import com.jwd.lunchvote.core.ui.base.ViewModelContract
import com.jwd.lunchvote.presentation.util.UiText
import kotlinx.parcelize.Parcelize

class HomeJoinContract {
  @Parcelize
  data class HomeJoinState(
    val loungeId: String = ""
  ) : ViewModelContract.State, Parcelable

  sealed interface HomeJoinEvent : ViewModelContract.Event {
    data class OnLoungeIdChange(val loungeId: String) : HomeJoinEvent
    data object OnClickDismissButton : HomeJoinEvent
    data object OnClickConfirmButton : HomeJoinEvent
  }

  sealed interface HomeJoinReduce : ViewModelContract.Reduce {
    data class UpdateLoungeId(val loungeId: String) : HomeJoinReduce
  }

  sealed interface HomeJoinSideEffect : ViewModelContract.SideEffect {
    data object PopBackStack : HomeJoinSideEffect
    data class NavigateToLounge(val loungeId: String) : HomeJoinSideEffect
    data class ShowSnackBar(val message: UiText) : HomeJoinSideEffect
  }
}