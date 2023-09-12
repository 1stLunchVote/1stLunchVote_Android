package com.jwd.lunchvote.core.ui.base

import android.os.Parcelable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

abstract class BaseStateViewModel<S : ViewModelContract.State, E : ViewModelContract.Event,
        R: ViewModelContract.Reduce, SE : ViewModelContract.SideEffect, DS: ViewModelContract.DialogState>(
    private val stateHandler: SavedStateHandle
) : ViewModel()
{
    protected abstract fun createInitialState(savedState: Parcelable?): S

    private val initialState : S by lazy {
        createInitialState(stateHandler[STATE_KEY])
    }

    private val _viewState = MutableStateFlow(initialState)
    val viewState : StateFlow<S> = _viewState.asStateFlow()
    protected val currentState : S get() = _viewState.value

    private val _events = MutableSharedFlow<E>()
    private val _reduce = MutableSharedFlow<R>()

    private val _sideEffect: Channel<SE> = Channel()
    val sideEffect = _sideEffect.receiveAsFlow()

    private val _dialogState = MutableStateFlow<DS?>(null)
    val dialogState : StateFlow<DS?> = _dialogState.asStateFlow()

    private val _error: MutableSharedFlow<String> = MutableSharedFlow()
    val error: SharedFlow<String> = _error.asSharedFlow()

    init {
        _events.onEach(::handleEvents)
            .launchIn(viewModelScope)
        _reduce.onEach{
            _viewState.value = reduceState(currentState, it).also { state ->
                stateHandler[STATE_KEY] = state.toParcelable()
            }
        }.launchIn(viewModelScope)
    }

    val ceh = CoroutineExceptionHandler { _, throwable ->
        throwable.printStackTrace()
        setError(throwable.message ?: "")
    }

    inline fun launch(crossinline action: suspend CoroutineScope.() -> Unit): Job {
        return viewModelScope.launch(ceh) {
            action(this)
        }
    }

    protected fun updateState(reduce: R){
        viewModelScope.launch {
            _reduce.emit(reduce)
        }
    }

    protected fun sendSideEffect(effect: SE) {
        viewModelScope.launch {
            _sideEffect.send(effect)
        }
    }

    fun sendEvent(event: E) {
        viewModelScope.launch {
            _events.emit(event)
        }
    }

    protected fun toggleDialog(dialogState: DS?){
        viewModelScope.launch {
            _dialogState.value = dialogState
        }
    }

    protected fun setError(errorMsg: String) = viewModelScope.launch {
        _error.emit(errorMsg)
    }

    abstract fun handleEvents(event: E)

    abstract fun reduceState(state: S, reduce: R) : S

    companion object{
        const val STATE_KEY = "viewState"
    }
}

typealias Reducer<S> = (state: ViewModelContract.State, action: ViewModelContract.Reduce) -> S