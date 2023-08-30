package com.jwd.lunchvote.ui.home

import android.os.Parcelable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.jwd.lunchvote.core.ui.base.BaseStateViewModel
import com.jwd.lunchvote.domain.usecase.lounge.CheckLoungeUseCase
import com.jwd.lunchvote.ui.home.HomeContract.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
  private val checkLoungeUseCase: CheckLoungeUseCase,
  savedStateHandle: SavedStateHandle
): BaseStateViewModel<HomeState, HomeEvent, HomeReduce, HomeSideEffect, HomeDialogState>(savedStateHandle){
  override fun createInitialState(savedState: Parcelable?): HomeState {
    return savedState as? HomeState ?: HomeState()
  }

  override fun handleEvents(event: HomeEvent) {
    when(event) {
      is HomeEvent.OnClickLoungeButton -> sendSideEffect(HomeSideEffect.NavigateToLounge(null))
      is HomeEvent.OnClickJoinLoungeButton -> toggleDialog(HomeDialogState.JoinDialog {
        updateState(HomeReduce.UpdateLoading(true))
        checkLoungeExist(it)
      })
      is HomeEvent.OnClickDismissButton -> toggleDialog(null)
      is HomeEvent.OnClickTemplateButton -> sendSideEffect(HomeSideEffect.NavigateToTemplateList)
      is HomeEvent.OnClickSettingButton -> sendSideEffect(HomeSideEffect.NavigateToSetting)
      is HomeEvent.OnClickTipsButton -> sendSideEffect(HomeSideEffect.NavigateToTips)
    }
  }

  override fun reduceState(state: HomeState, reduce: HomeReduce): HomeState {
    return when(reduce) {
      is HomeReduce.UpdateLoading -> state.copy(loading = reduce.loading)
    }
  }

  private fun checkLoungeExist(loungeId: String) {
    checkLoungeUseCase(loungeId)
      .onEach {
        if (it) sendSideEffect(HomeSideEffect.NavigateToLounge(loungeId))
        else sendSideEffect(HomeSideEffect.ShowSnackBar("존재하지 않는 방입니다."))
        updateState(HomeReduce.UpdateLoading(false))
      }
      .catch {
        sendSideEffect(HomeSideEffect.ShowSnackBar("오류가 발생하였습니다."))
        updateState(HomeReduce.UpdateLoading(false))
      }
      .launchIn(viewModelScope)
  }
}