package com.jwd.lunchvote.ui.home

import android.os.Parcelable
import com.jwd.lunchvote.core.ui.base.ViewModelContract
import kotlinx.parcelize.Parcelize

class HomeContract {
  @Parcelize
  data class HomeState(
    val loading: Boolean = false
  ): ViewModelContract.State, Parcelable {
    override fun toParcelable(): Parcelable = this
  }

  sealed interface HomeEvent: ViewModelContract.Event {
    data object OnClickLoungeButton : HomeEvent
    data object OnClickJoinLoungeButton : HomeEvent
    data object OnClickDismissButton : HomeEvent
    data object OnClickTemplateButton : HomeEvent
    data object OnClickSettingButton : HomeEvent
    data object OnClickTipsButton : HomeEvent
  }

  sealed interface HomeReduce : ViewModelContract.Reduce {
    data class UpdateLoading(val loading: Boolean) : HomeReduce
  }

  sealed interface HomeSideEffect: ViewModelContract.SideEffect {
    data class NavigateToLounge(val loungeId: String?) : HomeSideEffect
    data object NavigateToTemplateList : HomeSideEffect
    data object NavigateToSetting : HomeSideEffect
    data object NavigateToTips : HomeSideEffect
    data class ShowSnackBar(val message: String) : HomeSideEffect
  }

  sealed interface HomeDialogState: ViewModelContract.DialogState {
    data class JoinDialog(val confirm: (String) -> Unit) : HomeDialogState
  }
}