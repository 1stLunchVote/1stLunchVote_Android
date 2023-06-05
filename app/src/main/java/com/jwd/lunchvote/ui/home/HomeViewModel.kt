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
        when(event){
            is HomeEvent.OnCreateLounge -> {
                sendSideEffect(HomeSideEffect.NavigateToLounge(null))
            }
            is HomeEvent.OnJoinLounge -> {
                // Todo : Test용 방 번호임
                sendSideEffect(HomeSideEffect.NavigateToLounge("KqND4zmJ59"))
            }
        }
    }

    override fun reduceState(state: HomeState, reduce: HomeReduce): HomeState {
        TODO("Not yet implemented")
    }
}