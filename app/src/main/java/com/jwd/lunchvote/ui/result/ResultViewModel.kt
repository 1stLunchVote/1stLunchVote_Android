package com.jwd.lunchvote.ui.result

import android.os.Parcelable
import androidx.lifecycle.SavedStateHandle
import com.jwd.lunchvote.core.ui.base.BaseStateViewModel
import com.jwd.lunchvote.ui.result.ResultContract.*
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ResultViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
): BaseStateViewModel<ResultState, ResultEvent, ResultReduce, ResultSideEffect, ResultDialogState>(savedStateHandle) {
    override fun createInitialState(savedState: Parcelable?): ResultState {
        return savedState as? ResultState ?: ResultState()
    }

    override fun reduceState(state: ResultState, reduce: ResultReduce): ResultState {
        TODO("Not yet implemented")
    }

    override fun handleEvents(event: ResultEvent) {
        TODO("Not yet implemented")
    }
}