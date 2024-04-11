package com.jwd.lunchvote.presentation.ui.result

import android.os.Parcelable
import androidx.lifecycle.SavedStateHandle
import com.jwd.lunchvote.core.ui.base.BaseStateViewModel
import com.jwd.lunchvote.presentation.ui.result.ResultContract.ResultEvent
import com.jwd.lunchvote.presentation.ui.result.ResultContract.ResultReduce
import com.jwd.lunchvote.presentation.ui.result.ResultContract.ResultSideEffect
import com.jwd.lunchvote.presentation.ui.result.ResultContract.ResultState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ResultViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
): BaseStateViewModel<ResultState, ResultEvent, ResultReduce, ResultSideEffect>(savedStateHandle) {
    override fun createInitialState(savedState: Parcelable?): ResultState {
        return savedState as? ResultState ?: ResultState()
    }

    override fun reduceState(state: ResultState, reduce: ResultReduce): ResultState {
        TODO("Not yet implemented")
    }

    override fun handleEvents(event: ResultEvent) {
        TODO("Not yet implemented")
    }

    override fun handleErrors(error: Throwable) {
        TODO("Not yet implemented")
    }
}