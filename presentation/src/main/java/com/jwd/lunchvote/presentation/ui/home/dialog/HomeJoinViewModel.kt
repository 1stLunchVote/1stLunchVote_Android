package com.jwd.lunchvote.presentation.ui.home.dialog

import android.os.Parcelable
import androidx.lifecycle.SavedStateHandle
import com.jwd.lunchvote.core.common.error.LoungeError
import com.jwd.lunchvote.core.common.error.UnknownError
import com.jwd.lunchvote.core.ui.base.BaseStateViewModel
import com.jwd.lunchvote.domain.usecase.CheckLoungeUseCase
import com.jwd.lunchvote.presentation.ui.home.dialog.HomeJoinContract.HomeJoinEvent
import com.jwd.lunchvote.presentation.ui.home.dialog.HomeJoinContract.HomeJoinReduce
import com.jwd.lunchvote.presentation.ui.home.dialog.HomeJoinContract.HomeJoinSideEffect
import com.jwd.lunchvote.presentation.ui.home.dialog.HomeJoinContract.HomeJoinState
import com.jwd.lunchvote.presentation.util.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeJoinViewModel @Inject constructor(
  private val checkLoungeUseCase: CheckLoungeUseCase,
  savedStateHandle: SavedStateHandle
): BaseStateViewModel<HomeJoinState, HomeJoinEvent, HomeJoinReduce, HomeJoinSideEffect>(savedStateHandle){
  override fun createInitialState(savedState: Parcelable?): HomeJoinState {
    return savedState as? HomeJoinState ?: HomeJoinState()
  }

  override fun handleEvents(event: HomeJoinEvent) {
    when(event) {
      is HomeJoinEvent.OnLoungeIdChange -> updateState(HomeJoinReduce.UpdateLoungeId(event.loungeId))
      is HomeJoinEvent.OnClickDismissButton -> sendSideEffect(HomeJoinSideEffect.PopBackStack)
      is HomeJoinEvent.OnClickConfirmButton -> launch { checkLoungeExist() }
    }
  }

  override fun reduceState(state: HomeJoinState, reduce: HomeJoinReduce): HomeJoinState {
    return when(reduce) {
      is HomeJoinReduce.UpdateLoungeId -> state.copy(loungeId = reduce.loungeId)
    }
  }

  override fun handleErrors(error: Throwable) {
    sendSideEffect(HomeJoinSideEffect.ShowSnackBar(UiText.DynamicString(error.message ?: UnknownError.UNKNOWN)))
    when(error) {
      is LoungeError.NoLounge -> sendSideEffect(HomeJoinSideEffect.PopBackStack)
    }
  }

  private suspend fun checkLoungeExist() {
    val isAvailable = checkLoungeUseCase(currentState.loungeId)

    if (isAvailable) sendSideEffect(HomeJoinSideEffect.NavigateToLounge(currentState.loungeId))
    else throw LoungeError.NoLounge
  }
}