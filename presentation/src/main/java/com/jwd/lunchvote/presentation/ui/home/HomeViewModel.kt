package com.jwd.lunchvote.presentation.ui.home

import android.os.Parcelable
import androidx.lifecycle.SavedStateHandle
import com.jwd.lunchvote.core.common.error.UnknownError
import com.jwd.lunchvote.core.ui.base.BaseStateViewModel
import com.jwd.lunchvote.domain.usecase.GetFoodTrendUseCase
import com.jwd.lunchvote.presentation.mapper.asUI
import com.jwd.lunchvote.presentation.ui.home.HomeContract.HomeEvent
import com.jwd.lunchvote.presentation.ui.home.HomeContract.HomeReduce
import com.jwd.lunchvote.presentation.ui.home.HomeContract.HomeSideEffect
import com.jwd.lunchvote.presentation.ui.home.HomeContract.HomeState
import com.jwd.lunchvote.presentation.util.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
  private val getFoodTrendUseCase: GetFoodTrendUseCase,
  savedStateHandle: SavedStateHandle
): BaseStateViewModel<HomeState, HomeEvent, HomeReduce, HomeSideEffect>(savedStateHandle){
  override fun createInitialState(savedState: Parcelable?): HomeState {
    return savedState as? HomeState ?: HomeState()
  }

  init {
    launch {
      getFoodTrend()
    }
  }

  override fun handleEvents(event: HomeEvent) {
    when(event) {
      is HomeEvent.OnClickLoungeButton -> sendSideEffect(HomeSideEffect.NavigateToLounge)
      is HomeEvent.OnClickJoinLoungeButton -> sendSideEffect(HomeSideEffect.OpenJoinDialog)
      is HomeEvent.OnClickTemplateButton -> sendSideEffect(HomeSideEffect.NavigateToTemplateList)
      is HomeEvent.OnClickSettingButton -> sendSideEffect(HomeSideEffect.NavigateToSetting)
      is HomeEvent.OnClickTipsButton -> sendSideEffect(HomeSideEffect.NavigateToTips)
    }
  }

  override fun reduceState(state: HomeState, reduce: HomeReduce): HomeState {
    return when(reduce) {
      is HomeReduce.UpdateFoodTrend -> state.copy(foodTrend = reduce.foodTrend)
      is HomeReduce.UpdateFoodTrendRatio -> state.copy(foodTrendRatio = reduce.foodTrendRatio)
    }
  }

  override fun handleErrors(error: Throwable) {
    sendSideEffect(HomeSideEffect.ShowSnackBar(UiText.DynamicString(error.message ?: UnknownError.UNKNOWN)))
  }

  private suspend fun getFoodTrend() {
    val foodTrend = getFoodTrendUseCase()

    updateState(HomeReduce.UpdateFoodTrend(foodTrend.first.asUI()))
    updateState(HomeReduce.UpdateFoodTrendRatio(foodTrend.second))
  }
}