package com.jwd.lunchvote.presentation.screen.setting

import android.app.Activity
import android.os.Parcelable
import com.jwd.lunchvote.presentation.base.ViewModelContract
import com.jwd.lunchvote.presentation.util.UiText
import kotlinx.parcelize.Parcelize

class SettingContract {
  @Parcelize
  data class SettingState(
    val appVersion: String = ""
  ): ViewModelContract.State, Parcelable {
    override fun toParcelable(): Parcelable = this
  }

  sealed interface SettingEvent: ViewModelContract.Event {
    data class ScreenInitialize(val appVersion: String): SettingEvent

    data object OnClickBackButton: SettingEvent
    data object OnClickProfileButton: SettingEvent
    data object OnClickContactButton: SettingEvent
    data class OnClickSuggestButton(val activity: Activity): SettingEvent
    data class OnClickPolicyButton(val activity: Activity): SettingEvent
    data object OnClickLogoutButton: SettingEvent
  }

  sealed interface SettingReduce : ViewModelContract.Reduce {
    data class UpdateAppVersion(val appVersion: String) : SettingReduce
  }

  sealed interface SettingSideEffect: ViewModelContract.SideEffect {
    data object PopBackStack : SettingSideEffect
    data object NavigateToProfile : SettingSideEffect
    data object NavigateToContactList : SettingSideEffect
    data object NavigateToLogin : SettingSideEffect
    data class ShowSnackbar(val message: UiText) : SettingSideEffect
  }
}