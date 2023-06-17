package com.jwd.lunchvote.ui.home

import android.os.Parcelable
import androidx.lifecycle.SavedStateHandle
import com.jwd.lunchvote.core.ui.base.BaseStateViewModel
import com.jwd.lunchvote.ui.home.HomeContract.*
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
): BaseStateViewModel<HomeState, HomeEvent, HomeReduce, HomeSideEffect>(savedStateHandle){
    override fun createInitialState(savedState: Parcelable?): HomeState {
        return savedState as? HomeState ?: HomeState()
    }

    override fun handleEvents(event: HomeEvent) {
        when(event) {
            is HomeEvent.OnClickLoungeButton -> {
                sendSideEffect(HomeSideEffect.NavigateToLounge)
            }
            is HomeEvent.OnClickJoinLoungeButton -> {
                updateState(HomeReduce.ShowJoinDialog)
            }
            is HomeEvent.OnClickDismissButtonOfJoinDialog -> {
                updateState(HomeReduce.DismissJoinDialog)
            }
            is HomeEvent.SetJoinCode -> {
                updateState(HomeReduce.UpdateJoinCode(event.code))
            }
            is HomeEvent.OnClickTemplateButton -> {
                sendSideEffect(HomeSideEffect.NavigateToTemplate)
            }
            is HomeEvent.OnClickSettingButton -> {
                sendSideEffect(HomeSideEffect.NavigateToSetting)
            }
            is HomeEvent.OnClickTipsButton -> {
                sendSideEffect(HomeSideEffect.NavigateToTips)
            }
        }
    }

    override fun reduceState(state: HomeState, reduce: HomeReduce): HomeState {
        return when (reduce) {
            is HomeReduce.ShowJoinDialog -> {
                state.copy(showJoinDialog = true)
            }
            is HomeReduce.DismissJoinDialog -> {
                state.copy(showJoinDialog = false)
            }
            is HomeReduce.UpdateJoinCode -> {
                state.copy(code = reduce.code)
            }
        }
    }
}