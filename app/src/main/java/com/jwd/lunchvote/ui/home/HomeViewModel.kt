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
        when(event){
            is HomeEvent.OnCreateLounge -> {
                sendSideEffect(HomeSideEffect.NavigateToLounge(null))
            }
            is HomeEvent.OnJoinLounge -> {
                // Todo : Test용 방 번호임
                joinLoungeUseCase("qLu5O6jEBD")
                    .catch {
                        if (it is NotFoundException){
                            // Todo : 다이얼로그로 띄우기
                            sendSideEffect(HomeSideEffect.ShowSnackBar("존재하지 않는 방입니다."))
                        }
                    }
                    .onEach {
                        sendSideEffect(HomeSideEffect.NavigateToLounge("qLu5O6jEBD"))
                    }
                    .launchIn(viewModelScope)
            }
        }
    }

    override fun reduceState(state: HomeState, reduce: HomeReduce): HomeState {
        TODO("Not yet implemented")
    }
}