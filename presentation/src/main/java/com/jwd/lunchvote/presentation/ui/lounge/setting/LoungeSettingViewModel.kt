package com.jwd.lunchvote.presentation.ui.lounge.setting

import android.os.Parcelable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.jwd.lunchvote.core.ui.base.BaseStateViewModel
import com.jwd.lunchvote.presentation.ui.lounge.setting.LoungeSettingContract.LoungeSettingEvent
import com.jwd.lunchvote.presentation.ui.lounge.setting.LoungeSettingContract.LoungeSettingReduce
import com.jwd.lunchvote.presentation.ui.lounge.setting.LoungeSettingContract.LoungeSettingSideEffect
import com.jwd.lunchvote.presentation.ui.lounge.setting.LoungeSettingContract.LoungeSettingState
import com.jwd.lunchvote.presentation.util.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoungeSettingViewModel @Inject constructor(
  savedStateHandle: SavedStateHandle
) : BaseStateViewModel<LoungeSettingState, LoungeSettingEvent, LoungeSettingReduce, LoungeSettingSideEffect>(savedStateHandle) {
  override fun createInitialState(savedState: Parcelable?): LoungeSettingState {
    return savedState as? LoungeSettingState ?: LoungeSettingState()
  }

  private val _dialogState = MutableStateFlow("")
  val dialogState: StateFlow<String> = _dialogState.asStateFlow()
  fun openDialog(dialogState: String) {
    viewModelScope.launch {
      _dialogState.emit(dialogState)
    }
  }

  override fun handleEvents(event: LoungeSettingEvent) {
    when (event) {
      is LoungeSettingEvent.OnClickBackButton -> Unit
    }
  }

  override fun reduceState(state: LoungeSettingState, reduce: LoungeSettingReduce): LoungeSettingState {
    return when (reduce) {
      is LoungeSettingReduce.UpdateText -> state.copy(text = reduce.text)
    }
  }

  override fun handleErrors(error: Throwable) {
    sendSideEffect(LoungeSettingSideEffect.ShowSnackbar(UiText.ErrorString(error)))
  }
}