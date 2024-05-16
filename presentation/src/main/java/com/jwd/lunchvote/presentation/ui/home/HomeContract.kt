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
    val foodTrendRatio: Float = 0f,
    val loungeId: String? = null
  ) : ViewModelContract.State, Parcelable

  sealed interface HomeEvent : ViewModelContract.Event {
    data object ScreenInitialize : HomeEvent
    data object OnClickLoungeButton : HomeEvent
    data object OnClickJoinLoungeButton : HomeEvent
    data object OnClickTemplateButton : HomeEvent
    data object OnClickSettingButton : HomeEvent
    data object OnClickTipsButton : HomeEvent

    // DialogEvents
    data class OnLoungeIdChange(val loungeId: String) : HomeEvent
    data object OnClickCancelButtonJoinDialog : HomeEvent
    data object OnClickConfirmButtonJoinDialog : HomeEvent
  }

  sealed interface HomeReduce : ViewModelContract.Reduce {
    data class UpdateFoodTrend(val foodTrend: FoodUIModel) : HomeReduce
    data class UpdateFoodTrendRatio(val foodTrendRatio: Float) : HomeReduce
    data class UpdateLoungeId(val loungeId: String?) : HomeReduce
  }

  sealed interface HomeSideEffect : ViewModelContract.SideEffect {
    data class NavigateToLounge(val loungeId: String?) : HomeSideEffect
    data object NavigateToTemplateList : HomeSideEffect
    data object NavigateToSetting : HomeSideEffect
    data object NavigateToTips : HomeSideEffect
    data object OpenJoinDialog : HomeSideEffect
    data object CloseDialog : HomeSideEffect
    data class ShowSnackBar(val message: UiText) : HomeSideEffect
  }

  companion object {
    const val JOIN_DIALOG = "join_dialog"
  }
}