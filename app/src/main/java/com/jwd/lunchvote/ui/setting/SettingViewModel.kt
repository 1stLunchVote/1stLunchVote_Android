package com.jwd.lunchvote.ui.setting

import android.os.Parcelable
import androidx.lifecycle.SavedStateHandle
import com.jwd.lunchvote.core.ui.base.BaseStateViewModel
import com.jwd.lunchvote.ui.setting.SettingContract.SettingDialogState
import com.jwd.lunchvote.ui.setting.SettingContract.SettingEvent
import com.jwd.lunchvote.ui.setting.SettingContract.SettingReduce
import com.jwd.lunchvote.ui.setting.SettingContract.SettingSideEffect
import com.jwd.lunchvote.ui.setting.SettingContract.SettingState
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
    }
  }

  override fun reduceState(state: SettingState, reduce: SettingReduce): SettingState {
    return when(reduce) {
      else -> state
    }
  }
}