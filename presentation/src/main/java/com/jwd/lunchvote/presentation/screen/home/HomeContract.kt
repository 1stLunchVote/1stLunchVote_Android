package com.jwd.lunchvote.presentation.screen.home

import android.content.Context
import android.net.Uri
import android.os.Parcelable
import com.jwd.lunchvote.presentation.base.ViewModelContract
import com.jwd.lunchvote.presentation.model.FoodUIModel
import com.jwd.lunchvote.presentation.util.UiText
import kotlinx.parcelize.Parcelize

class HomeContract {
  @Parcelize
  data class HomeState(
    val foodTrend: FoodUIModel? = null,
    val foodTrendRatio: Float = 0f,
    val joinDialogState: JoinDialogState? = null,
    val secretDialogState: SecretDialogState? = null,
  ) : ViewModelContract.State, Parcelable

  sealed interface HomeEvent : ViewModelContract.Event {
    data object ScreenInitialize : HomeEvent
    data object OnClickLoungeButton : HomeEvent
    data object OnClickJoinLoungeButton : HomeEvent
    data object OnClickTemplateButton : HomeEvent
    data object OnClickFriendButton : HomeEvent
    data object OnClickSettingButton : HomeEvent
    data object OnClickTipsButton : HomeEvent
    data object OnLongPressIcon : HomeEvent
  }

  sealed interface HomeReduce : ViewModelContract.Reduce {
    data class UpdateFoodTrend(val foodTrend: FoodUIModel?) : HomeReduce
    data class UpdateFoodTrendRatio(val foodTrendRatio: Float) : HomeReduce
    data class UpdateJoinDialogState(val joinDialogState: JoinDialogState?) : HomeReduce
    data class UpdateSecretDialogState(val secretDialogState: SecretDialogState?) : HomeReduce
  }

  sealed interface HomeSideEffect : ViewModelContract.SideEffect {
    data class NavigateToLounge(val loungeId: String?) : HomeSideEffect
    data object NavigateToTemplateList : HomeSideEffect
    data object NavigateToFriendList : HomeSideEffect
    data object NavigateToSetting : HomeSideEffect
    data object NavigateToTips : HomeSideEffect
    data class ShowSnackbar(val message: UiText) : HomeSideEffect
  }

  @Parcelize
  data class JoinDialogState(
    val loungeId: String = ""
  ) : ViewModelContract.State, Parcelable {
    override fun toParcelable(): Parcelable = this
  }

  sealed interface JoinDialogEvent : HomeEvent {
    data class OnLoungeIdChange(val loungeId: String) : JoinDialogEvent
    data object OnClickCancelButton : JoinDialogEvent
    data object OnClickConfirmButton : JoinDialogEvent
  }

  sealed interface JoinDialogReduce : HomeReduce {
    data class UpdateLoungeId(val loungeId: String) : JoinDialogReduce
  }

  @Parcelize
  data class SecretDialogState(
    val foodName: String = "",
    val foodImageUri: Uri = Uri.EMPTY,
  ) : ViewModelContract.State, Parcelable {
    override fun toParcelable(): Parcelable = this
  }

  sealed interface SecretDialogEvent : HomeEvent {
    data class OnFoodNameChange(val foodName: String) : SecretDialogEvent
    data class OnFoodImageChange(val foodImageUri: Uri) : SecretDialogEvent
    data object OnImageLoadError : SecretDialogEvent
    data object OnClickCancelButton : SecretDialogEvent
    data class OnClickUploadButton(val context: Context) : SecretDialogEvent
  }

  sealed interface SecretDialogReduce : HomeReduce {
    data class UpdateFoodName(val foodName: String) : SecretDialogReduce
    data class UpdateFoodImageUri(val foodImageUri: Uri) : SecretDialogReduce
  }
}