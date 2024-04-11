package com.jwd.lunchvote.presentation.ui.home

import android.os.Parcelable
import com.jwd.lunchvote.core.ui.base.ViewModelContract
import com.jwd.lunchvote.presentation.util.UiText
import kotlinx.parcelize.Parcelize

class HomeContract {
  @Parcelize
  class HomeState : ViewModelContract.State, Parcelable

  sealed interface HomeEvent : ViewModelContract.Event {
    data object OnClickLoungeButton : HomeEvent
    data object OnClickJoinLoungeButton : HomeEvent
    data object OnClickTemplateButton : HomeEvent
    data object OnClickSettingButton : HomeEvent
    data object OnClickTipsButton : HomeEvent
  }

  sealed interface HomeReduce : ViewModelContract.Reduce

  sealed interface HomeSideEffect : ViewModelContract.SideEffect {
    data class NavigateToLounge(val loungeId: String?) : HomeSideEffect
    data object NavigateToTemplateList : HomeSideEffect
    data object NavigateToSetting : HomeSideEffect
    data object NavigateToTips : HomeSideEffect
    data object OpenJoinDialog : HomeSideEffect
    data class ShowSnackBar(val message: UiText) : HomeSideEffect
  }
}