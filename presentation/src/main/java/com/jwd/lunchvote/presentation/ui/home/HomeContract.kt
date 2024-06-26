package com.jwd.lunchvote.presentation.ui.home

import android.content.Context
import android.net.Uri
import android.os.Parcelable
import com.jwd.lunchvote.core.ui.base.ViewModelContract
import com.jwd.lunchvote.presentation.model.FoodUIModel
import com.jwd.lunchvote.presentation.util.UiText
import kotlinx.parcelize.Parcelize

class HomeContract {
  @Parcelize
  data class HomeState(
    val foodTrend: FoodUIModel? = null,
    val foodTrendRatio: Float = 0f,
    val loungeId: String? = null,

    // TODO: Temporary Secret State
    val foodName: String? = null,
    val foodImageUri: Uri? = null,
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

    // TODO: Temporary Secret Events
    data object OnClickSecretButton : HomeEvent
    data class OnFoodNameChangeOfSecretDialog(val foodName: String?) : HomeEvent
    data class OnFoodImageChangeOfSecretDialog(val foodImageUri: Uri?) : HomeEvent
    data object OnImageLoadErrorOfSecretDialog : HomeEvent
    data object OnClickCancelButtonOfSecretDialog : HomeEvent
    data class OnClickUploadButtonOfSecretDialog(val context: Context) : HomeEvent
  }

  sealed interface HomeReduce : ViewModelContract.Reduce {
    data class UpdateFoodTrend(val foodTrend: FoodUIModel) : HomeReduce
    data class UpdateFoodTrendRatio(val foodTrendRatio: Float) : HomeReduce
    data class UpdateLoungeId(val loungeId: String?) : HomeReduce

    // TODO: Temporary Secret Reduces
    data class UpdateFoodName(val foodName: String?) : HomeReduce
    data class UpdateFoodImageUri(val foodImageUri: Uri?) : HomeReduce
  }

  sealed interface HomeSideEffect : ViewModelContract.SideEffect {
    data class NavigateToLounge(val loungeId: String?) : HomeSideEffect
    data object NavigateToTemplateList : HomeSideEffect
    data object NavigateToSetting : HomeSideEffect
    data object NavigateToTips : HomeSideEffect
    data object OpenJoinDialog : HomeSideEffect
    data object CloseDialog : HomeSideEffect
    data class ShowSnackbar(val message: UiText) : HomeSideEffect

    // TODO: Temporary Secret SideEffects
    data object OpenSecretDialog : HomeSideEffect
  }

  companion object {
    const val JOIN_DIALOG = "join_dialog"

    // TODO: Temporary Secret Dialog
    const val SECRET_DIALOG = "secret_dialog"
  }
}