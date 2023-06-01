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
        TODO("Not yet implemented")
    }

    override fun reduceState(state: HomeState, reduce: HomeReduce): HomeState {
        TODO("Not yet implemented")
    }
}