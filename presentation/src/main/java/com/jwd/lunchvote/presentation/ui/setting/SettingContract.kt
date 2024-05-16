package com.jwd.lunchvote.presentation.ui.setting

import android.os.Parcelable
import com.jwd.lunchvote.core.ui.base.ViewModelContract
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
    data object OnClickAlertSettingButton: SettingEvent
    data object OnClickContactButton: SettingEvent
    data object OnClickNoticeButton: SettingEvent
    data object OnClickSuggestButton: SettingEvent
    data object OnClickLogoutButton: SettingEvent
  }

  sealed interface SettingReduce : ViewModelContract.Reduce {
    data class UpdateAppVersion(val appVersion: String) : SettingReduce
  }

  sealed interface SettingSideEffect: ViewModelContract.SideEffect {
    data object PopBackStack : SettingSideEffect
    data object NavigateToProfile : SettingSideEffect
    data object NavigateToLogin : SettingSideEffect
    data class ShowSnackBar(val message: UiText) : SettingSideEffect
  }
}