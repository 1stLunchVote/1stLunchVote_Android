package com.jwd.lunchvote.presentation.ui.home

import android.os.Parcelable
import com.jwd.lunchvote.core.ui.base.ViewModelContract
import com.jwd.lunchvote.presentation.model.FoodUIModel
import com.jwd.lunchvote.presentation.util.UiText
import kotlinx.parcelize.Parcelize

class HomeContract {
  @Parcelize
  data class HomeState(
    val foodTrend: FoodUIModel = FoodUIModel(),
    val foodTrendRatio: Float = 0f
  ) : ViewModelContract.State, Parcelable

  sealed interface HomeEvent : ViewModelContract.Event {
    data object OnClickLoungeButton : HomeEvent
    data object OnClickJoinLoungeButton : HomeEvent
    data object OnClickTemplateButton : HomeEvent
    data object OnClickSettingButton : HomeEvent
    data object OnClickTipsButton : HomeEvent
  }

  sealed interface HomeReduce : ViewModelContract.Reduce {
    data class UpdateFoodTrend(val foodTrend: FoodUIModel) : HomeReduce
    data class UpdateFoodTrendRatio(val foodTrendRatio: Float) : HomeReduce
  }

  sealed interface HomeSideEffect : ViewModelContract.SideEffect {
    data object NavigateToLounge : HomeSideEffect
    data object NavigateToTemplateList : HomeSideEffect
    data object NavigateToSetting : HomeSideEffect
    data object NavigateToTips : HomeSideEffect
    data object OpenJoinDialog : HomeSideEffect
    data class ShowSnackBar(val message: UiText) : HomeSideEffect
  }
}