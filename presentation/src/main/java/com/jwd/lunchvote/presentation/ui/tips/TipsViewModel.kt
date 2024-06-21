package com.jwd.lunchvote.presentation.ui.tips

import android.os.Parcelable
import androidx.lifecycle.SavedStateHandle
import com.jwd.lunchvote.core.ui.base.BaseStateViewModel
import com.jwd.lunchvote.presentation.ui.tips.TipsContract.TipsEvent
import com.jwd.lunchvote.presentation.ui.tips.TipsContract.TipsReduce
import com.jwd.lunchvote.presentation.ui.tips.TipsContract.TipsSideEffect
import com.jwd.lunchvote.presentation.ui.tips.TipsContract.TipsState
import com.jwd.lunchvote.presentation.util.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class TipsViewModel @Inject constructor(
  savedStateHandle: SavedStateHandle
): BaseStateViewModel<TipsState, TipsEvent, TipsReduce, TipsSideEffect>(savedStateHandle) {
  override fun createInitialState(savedState: Parcelable?): TipsState {
    return savedState as? TipsState ?: TipsState()
  }

  override fun handleEvents(event: TipsEvent) {
    when(event) {
      is TipsEvent.OnClickBackButton -> sendSideEffect(TipsSideEffect.PopBackStack)
      is TipsEvent.OnClickTab -> updateState(TipsReduce.UpdateTabIndex(event.tabIndex))
    }
  }

  override fun reduceState(state: TipsState, reduce: TipsReduce): TipsState {
    return when (reduce) {
      is TipsReduce.UpdateTabIndex -> state.copy(tabIndex = reduce.tabIndex)
    }
  }

  override fun handleErrors(error: Throwable) {
    sendSideEffect(TipsSideEffect.ShowSnackbar(UiText.ErrorString(error)))
  }
}