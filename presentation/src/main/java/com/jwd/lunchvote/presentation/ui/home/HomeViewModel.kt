package com.jwd.lunchvote.presentation.ui.home

import android.os.Parcelable
import androidx.lifecycle.SavedStateHandle
import com.jwd.lunchvote.core.common.base.error.UnknownError
import com.jwd.lunchvote.core.ui.base.BaseStateViewModel
import com.jwd.lunchvote.domain.usecase.lounge.CheckLoungeUseCase
import com.jwd.lunchvote.presentation.ui.home.HomeContract.HomeEvent
import com.jwd.lunchvote.presentation.ui.home.HomeContract.HomeReduce
import com.jwd.lunchvote.presentation.ui.home.HomeContract.HomeSideEffect
import com.jwd.lunchvote.presentation.ui.home.HomeContract.HomeState
import com.jwd.lunchvote.presentation.util.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
  private val checkLoungeUseCase: CheckLoungeUseCase,
  savedStateHandle: SavedStateHandle
): BaseStateViewModel<HomeState, HomeEvent, HomeReduce, HomeSideEffect>(savedStateHandle){
  override fun createInitialState(savedState: Parcelable?): HomeState {
    return savedState as? HomeState ?: HomeState()
  }

  override fun handleEvents(event: HomeEvent) {
    when(event) {
      is HomeEvent.OnClickLoungeButton -> sendSideEffect(HomeSideEffect.NavigateToLounge(null))
      is HomeEvent.OnClickJoinLoungeButton -> sendSideEffect(HomeSideEffect.OpenJoinDialog)
      is HomeEvent.OnClickTemplateButton -> sendSideEffect(HomeSideEffect.NavigateToTemplateList)
      is HomeEvent.OnClickSettingButton -> sendSideEffect(HomeSideEffect.NavigateToSetting)
      is HomeEvent.OnClickTipsButton -> sendSideEffect(HomeSideEffect.NavigateToTips)
    }
  }

  override fun reduceState(state: HomeState, reduce: HomeReduce): HomeState = state

  override fun handleErrors(error: Throwable) {
    sendSideEffect(HomeSideEffect.ShowSnackBar(UiText.DynamicString(error.message ?: UnknownError.UNKNOWN)))
  }

  private fun checkLoungeExist(loungeId: String) {
    launch(hasLoading = true) {
      runCatching { checkLoungeUseCase(loungeId) }
        .onSuccess {
          if (it) sendSideEffect(HomeSideEffect.NavigateToLounge(loungeId))
          else sendSideEffect(HomeSideEffect.ShowSnackBar(UiText.DynamicString("존재하지 않는 방입니다.")))
        }
        .onFailure {
          sendSideEffect(HomeSideEffect.ShowSnackBar(UiText.DynamicString("오류가 발생하였습니다.")))
        }
    }
  }
}