package com.jwd.lunchvote.presentation.ui.home

import android.os.Parcelable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.jwd.lunchvote.core.common.error.LoungeError
import com.jwd.lunchvote.core.common.error.UnknownError
import com.jwd.lunchvote.core.ui.base.BaseStateViewModel
import com.jwd.lunchvote.domain.repository.FoodRepository
import com.jwd.lunchvote.domain.repository.LoungeRepository
import com.jwd.lunchvote.presentation.R
import com.jwd.lunchvote.presentation.mapper.asUI
import com.jwd.lunchvote.presentation.ui.home.HomeContract.HomeEvent
import com.jwd.lunchvote.presentation.ui.home.HomeContract.HomeReduce
import com.jwd.lunchvote.presentation.ui.home.HomeContract.HomeSideEffect
import com.jwd.lunchvote.presentation.ui.home.HomeContract.HomeState
import com.jwd.lunchvote.presentation.util.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
  private val foodRepository: FoodRepository,
  private val loungeRepository: LoungeRepository,
  savedStateHandle: SavedStateHandle
): BaseStateViewModel<HomeState, HomeEvent, HomeReduce, HomeSideEffect>(savedStateHandle){
  override fun createInitialState(savedState: Parcelable?): HomeState {
    return savedState as? HomeState ?: HomeState()
  }

  private val _dialogState = MutableStateFlow("")
  val dialogState: StateFlow<String> = _dialogState.asStateFlow()
  fun setDialogState(dialogState: String) {
    viewModelScope.launch {
      _dialogState.emit(dialogState)
    }
  }

  override fun handleEvents(event: HomeEvent) {
    when(event) {
      is HomeEvent.ScreenInitialize -> launch { getFoodTrend() }
      is HomeEvent.OnClickLoungeButton -> sendSideEffect(HomeSideEffect.NavigateToLounge(currentState.loungeId))
      is HomeEvent.OnClickJoinLoungeButton -> sendSideEffect(HomeSideEffect.OpenJoinDialog)
      is HomeEvent.OnClickTemplateButton -> sendSideEffect(HomeSideEffect.NavigateToTemplateList)
      is HomeEvent.OnClickSettingButton -> sendSideEffect(HomeSideEffect.NavigateToSetting)
      is HomeEvent.OnClickTipsButton -> sendSideEffect(HomeSideEffect.NavigateToTips)

      // DialogEvent
      is HomeEvent.OnLoungeIdChange -> updateState(HomeReduce.UpdateLoungeId(event.loungeId))
      is HomeEvent.OnClickCancelButtonJoinDialog -> {
        updateState(HomeReduce.UpdateLoungeId(null))
        sendSideEffect(HomeSideEffect.CloseDialog)
      }
      is HomeEvent.OnClickConfirmButtonJoinDialog -> launch { checkLoungeExist() }
    }
  }

  override fun reduceState(state: HomeState, reduce: HomeReduce): HomeState {
    return when(reduce) {
      is HomeReduce.UpdateFoodTrend -> state.copy(foodTrend = reduce.foodTrend)
      is HomeReduce.UpdateFoodTrendRatio -> state.copy(foodTrendRatio = reduce.foodTrendRatio)
      is HomeReduce.UpdateLoungeId -> state.copy(loungeId = reduce.loungeId)
    }
  }

  override fun handleErrors(error: Throwable) {
    sendSideEffect(HomeSideEffect.ShowSnackBar(UiText.DynamicString(error.message ?: UnknownError.UNKNOWN)))
  }

  private suspend fun getFoodTrend() {
    val foodTrend = foodRepository.getFoodTrend()

    updateState(HomeReduce.UpdateFoodTrend(foodTrend.first.asUI()))
    updateState(HomeReduce.UpdateFoodTrendRatio(foodTrend.second))
  }

  private suspend fun checkLoungeExist() {
    val loungeId = currentState.loungeId ?: return
    updateState(HomeReduce.UpdateLoungeId(null))
    sendSideEffect(HomeSideEffect.CloseDialog)
    sendSideEffect(HomeSideEffect.ShowSnackBar(UiText.StringResource(R.string.home_joining_lounge_snackbar)))

    val isAvailable = loungeRepository.checkLoungeExistById(loungeId)

    if (isAvailable) sendSideEffect(HomeSideEffect.NavigateToLounge(loungeId))
    else throw LoungeError.NoLounge
  }
}