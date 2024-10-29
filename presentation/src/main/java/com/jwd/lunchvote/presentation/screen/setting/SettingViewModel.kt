package com.jwd.lunchvote.presentation.screen.setting

import android.content.Intent
import android.net.Uri
import android.os.Parcelable
import androidx.lifecycle.SavedStateHandle
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.jwd.lunchvote.core.ui.base.BaseStateViewModel
import com.jwd.lunchvote.domain.repository.StorageRepository
import com.jwd.lunchvote.presentation.R
import com.jwd.lunchvote.presentation.screen.setting.SettingContract.SettingEvent
import com.jwd.lunchvote.presentation.screen.setting.SettingContract.SettingReduce
import com.jwd.lunchvote.presentation.screen.setting.SettingContract.SettingSideEffect
import com.jwd.lunchvote.presentation.screen.setting.SettingContract.SettingState
import com.jwd.lunchvote.presentation.util.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kr.co.inbody.config.error.UserError
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(
  private val storageRepository: StorageRepository,
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
      is SettingEvent.OnClickContactButton -> sendSideEffect(SettingSideEffect.NavigateToContactList)
      is SettingEvent.OnClickSuggestButton -> {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
          data = Uri.parse("mailto:shrimp0266@gmail.com") // 이메일 주소
        }
        event.activity.startActivity(intent)
      }
      is SettingEvent.OnClickPolicyButton -> launch {
        val uri = storageRepository.getPrivacyPolicyUri()
        val intent = Intent(Intent.ACTION_VIEW).apply {
          data = Uri.parse(uri)
        }
        event.activity.startActivity(intent)
      }
      is SettingEvent.OnClickLogoutButton -> launch { logout() }
    }
  }

  override fun reduceState(state: SettingState, reduce: SettingReduce): SettingState {
    return when (reduce) {
      is SettingReduce.UpdateAppVersion -> state.copy(appVersion = reduce.appVersion)
    }
  }

  override fun handleErrors(error: Throwable) {
    sendSideEffect(SettingSideEffect.ShowSnackbar(UiText.ErrorString(error)))
    when (error) {
      is UserError.NoUser -> Firebase.auth.signOut()
    }
  }

  private fun logout() {
    Firebase.auth.signOut()

    sendSideEffect(SettingSideEffect.ShowSnackbar(UiText.StringResource(R.string.setting_logout_success_snackbar)))
    sendSideEffect(SettingSideEffect.NavigateToLogin)
  }
}