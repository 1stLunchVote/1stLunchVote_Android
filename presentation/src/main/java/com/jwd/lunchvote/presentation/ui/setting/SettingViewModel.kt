package com.jwd.lunchvote.presentation.ui.setting

import android.os.Parcelable
import androidx.lifecycle.SavedStateHandle
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.jwd.lunchvote.core.ui.base.BaseStateViewModel
import com.jwd.lunchvote.presentation.R
import com.jwd.lunchvote.presentation.ui.setting.SettingContract.SettingEvent
import com.jwd.lunchvote.presentation.ui.setting.SettingContract.SettingReduce
import com.jwd.lunchvote.presentation.ui.setting.SettingContract.SettingSideEffect
import com.jwd.lunchvote.presentation.ui.setting.SettingContract.SettingState
import com.jwd.lunchvote.presentation.util.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kr.co.inbody.config.error.UserError
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(
  savedStateHandle: SavedStateHandle
): BaseStateViewModel<SettingState, SettingEvent, SettingReduce, SettingSideEffect>(savedStateHandle) {
  override fun createInitialState(savedState: Parcelable?): SettingState {
    return savedState as? SettingState ?: SettingState()
  }

  override fun handleEvents(event: SettingEvent) {
    when(event) {
      is SettingEvent.ScreenInitialize -> updateState(SettingReduce.UpdateAppVersion(event.appVersion))

      is SettingEvent.OnClickBackButton -> sendSideEffect(SettingSideEffect.PopBackStack)
      is SettingEvent.OnClickProfileButton -> sendSideEffect(SettingSideEffect.NavigateToProfile)
      is SettingEvent.OnClickAlertSettingButton -> sendSideEffect(SettingSideEffect.ShowSnackBar(UiText.DynamicString("알림 설정")))
      is SettingEvent.OnClickContactButton -> sendSideEffect(SettingSideEffect.ShowSnackBar(UiText.DynamicString("1:1 문의")))
      is SettingEvent.OnClickNoticeButton -> sendSideEffect(SettingSideEffect.ShowSnackBar(UiText.DynamicString("공지사항 및 이용약관")))
      is SettingEvent.OnClickSuggestButton -> sendSideEffect(SettingSideEffect.ShowSnackBar(UiText.DynamicString("개선 제안하기")))
      is SettingEvent.OnClickLogoutButton -> launch { logout() }
    }
  }

  override fun reduceState(state: SettingState, reduce: SettingReduce): SettingState {
    return when (reduce) {
      is SettingReduce.UpdateAppVersion -> state.copy(appVersion = reduce.appVersion)
    }
  }

  override fun handleErrors(error: Throwable) {
    sendSideEffect(SettingSideEffect.ShowSnackBar(UiText.ErrorString(error)))
    when (error) {
      is UserError.NoUser -> Firebase.auth.signOut()
    }
  }

  private fun logout() {
    Firebase.auth.signOut()

    sendSideEffect(SettingSideEffect.ShowSnackBar(UiText.StringResource(R.string.setting_logout_success_snackbar)))
    sendSideEffect(SettingSideEffect.NavigateToLogin)
  }
}