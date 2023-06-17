package com.jwd.lunchvote.ui.home

import android.content.res.Resources.NotFoundException
import android.os.Parcelable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.jwd.lunchvote.core.ui.base.BaseStateViewModel
import com.jwd.lunchvote.domain.usecase.lounge.JoinLoungeUseCase
import com.jwd.lunchvote.ui.home.HomeContract.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val joinLoungeUseCase: JoinLoungeUseCase,
    savedStateHandle: SavedStateHandle
): BaseStateViewModel<HomeState, HomeEvent, HomeReduce, HomeSideEffect>(savedStateHandle){
    override fun createInitialState(savedState: Parcelable?): HomeState {
        return savedState as? HomeState ?: HomeState()
    }

    override fun handleEvents(event: HomeEvent) {
        when(event) {
            is HomeEvent.OnClickLoungeButton -> {
                sendSideEffect(HomeSideEffect.NavigateToLounge(null))
            }
            is HomeEvent.OnClickJoinLoungeButton -> {
                updateState(HomeReduce.ShowJoinDialog)
            }
            is HomeEvent.SetJoinCode -> {
                updateState(HomeReduce.UpdateJoinCode(event.code))
            }
            is HomeEvent.OnClickDismissButtonOfJoinDialog -> {
                updateState(HomeReduce.DismissJoinDialog)
            }
            is HomeEvent.OnClickConfirmButtonOfJoinDialog -> {
                updateState(HomeReduce.ConfirmJoinDialog(event.code))
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
            is HomeReduce.UpdateJoinCode -> {
                state.copy(code = reduce.code)
            }
            is HomeReduce.DismissJoinDialog -> {
                state.copy(showJoinDialog = false)
            }
            is HomeReduce.ConfirmJoinDialog -> {
                joinLoungeUseCase(reduce.code)
                    .catch {
                        if (it is NotFoundException) {
                            sendSideEffect(HomeSideEffect.ShowSnackBar("존재하지 않는 방입니다."))
                        }
                    }
                    .onEach {
                        sendSideEffect(HomeSideEffect.NavigateToLounge(reduce.code))
                    }
                    .launchIn(viewModelScope)
                state.copy(showJoinDialog = false)
            }
        }
    }
}