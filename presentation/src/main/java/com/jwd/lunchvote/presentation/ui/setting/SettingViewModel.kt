package com.jwd.lunchvote.presentation.ui.setting

import android.os.Parcelable
import androidx.lifecycle.SavedStateHandle
import com.jwd.lunchvote.core.ui.base.BaseStateViewModel
import com.jwd.lunchvote.presentation.ui.setting.SettingContract.SettingDialogState
import com.jwd.lunchvote.presentation.ui.setting.SettingContract.SettingEvent
import com.jwd.lunchvote.presentation.ui.setting.SettingContract.SettingReduce
import com.jwd.lunchvote.presentation.ui.setting.SettingContract.SettingSideEffect
import com.jwd.lunchvote.presentation.ui.setting.SettingContract.SettingState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(
  savedStateHandle: SavedStateHandle
): BaseStateViewModel<SettingState, SettingEvent, SettingReduce, SettingSideEffect, SettingDialogState>(savedStateHandle) {
  override fun createInitialState(savedState: Parcelable?): SettingState {
    return savedState as? SettingState ?: SettingState()
  }

  override fun handleEvents(event: SettingEvent) {
    when(event) {
      is SettingEvent.OnClickBackButton -> sendSideEffect(SettingSideEffect.PopBackStack(""))
      is SettingEvent.OnClickEditProfileButton -> sendSideEffect(SettingSideEffect.ShowSnackBar("프로필 수정"))
      is SettingEvent.OnClickAlertSettingButton -> sendSideEffect(SettingSideEffect.ShowSnackBar("알림 설정"))
      is SettingEvent.OnClickContactButton -> sendSideEffect(SettingSideEffect.ShowSnackBar("1:1 문의"))
      is SettingEvent.OnClickNoticeButton -> sendSideEffect(SettingSideEffect.ShowSnackBar("공지사항 및 이용약관"))
      is SettingEvent.OnClickSuggestButton -> sendSideEffect(SettingSideEffect.ShowSnackBar("개선 제안하기"))
      is SettingEvent.OnClickLogoutButton -> sendSideEffect(SettingSideEffect.ShowSnackBar("로그아웃"))
    }
  }

  override fun reduceState(state: SettingState, reduce: SettingReduce): SettingState = state
}