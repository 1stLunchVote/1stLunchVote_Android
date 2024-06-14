package com.jwd.lunchvote.core.ui.base

import android.os.Parcelable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jwd.lunchvote.core.ui.base.ViewModelContract.Event
import com.jwd.lunchvote.core.ui.base.ViewModelContract.Reduce
import com.jwd.lunchvote.core.ui.base.ViewModelContract.SideEffect
import com.jwd.lunchvote.core.ui.base.ViewModelContract.State
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

abstract class BaseStateViewModel<S : State, E : Event, R : Reduce, SE : SideEffect>(
  private val savedStateHandle: SavedStateHandle
) : ViewModel() {
  /**
   * 초기 상태를 받아오는 메서드
   * @param savedState 이전 상태
   * @return 초기 상태
   */
  protected abstract fun createInitialState(savedState: Parcelable?): S

  /**
   * 초기 상태
   *
   * 초기 상태를 savedStateHandle에 저장된 상태로부터 받아온다.
   */
  private val initialState: S by lazy {
    createInitialState(savedStateHandle[STATE_KEY])
  }

  private val _viewState = MutableStateFlow(initialState)
  /**
   * 상태(State)를 StateFlow로서 관리
   *
   * viewState를 collect함으로써 상태(State)의 갱신에 따라 UI를 업데이트할 수 있다.
   *
   * e.g.
   * ```
   * val state: SampleState by viewModel.viewState.collectAsStateWithLifecycle()
   */
  val viewState: StateFlow<S> = _viewState.asStateFlow()
  /**
   * ViewModel 내부에서 사용하는 현재 상태(State)의 Snapshot
   */
  protected val currentState: S get() = _viewState.value

  private val _events = MutableSharedFlow<E>()
  private val _reduce = MutableSharedFlow<R>()

  private val _sideEffect: Channel<SE> = Channel()
  /**
   * SideEffect를 Flow로서 관리
   *
   * sideEffect를 collect함으로써 부수 효과(Side Effect)에 따른 동작을 정의할 수 있다.
   *
   * e.g.
   * ```
   * LaunchedEffect(viewModel.sideEffect) {
   *   viewModel.sideEffect.collectLatest {
   *     when (it) {
   *       is SampleSideEffect.PopBackStack -> popBackStack()
   *     }
   *   }
   * }
   */
  val sideEffect: Flow<SE> = _sideEffect.receiveAsFlow()

  private val _error: Channel<Throwable> = Channel()
  /**
   * 에러가 발생할 경우 Flow를 통해 전달
   *
   * handleErrors 메서드를 통해 collect될 때마다 그에 맞는 처리를 할 수 있다.
   */
  val error: Flow<Throwable> = _error.receiveAsFlow()

  /**
   * ViewModel 내부 혹은 View에서 에러를 발생시켜 ceh가 catch하도록 하는 메서드
   * @param error 발생한 에러
   */
  fun throwError(error: Throwable) {
    viewModelScope.launch {
      _error.send(error)
    }
  }

  private val _isLoading = MutableStateFlow(false)
  /**
   * 현재 로딩 상태를 StateFlow로서 관리
   *
   * isLoading을 collect함으로써 로딩 상태에 따른 UI를 업데이트할 수 있다.
   */
  val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

  /**
   * ViewModel 내부 혹은 View에서 임의로 로딩 상태를 전송할 수 있는 메서드
   * @param isLoading 로딩 상태
   */
  fun setLoading(isLoading: Boolean) {
    viewModelScope.launch {
      _isLoading.emit(isLoading)
    }
  }

  init {
    _events.onEach(::handleEvents)
      .launchIn(viewModelScope)
    _reduce.onEach {
      _viewState.value = reduceState(currentState, it).also { state ->
        savedStateHandle[STATE_KEY] = state.toParcelable()
      }
    }.launchIn(viewModelScope)
    _error.receiveAsFlow()
      .onEach(::handleErrors)
      .launchIn(viewModelScope)
  }

  val ceh = CoroutineExceptionHandler { _, throwable ->
    throwable.printStackTrace()
    throwError(throwable)
    setLoading(false)
  }

  /**
   * BaseStateViewModel의 launch
   *
   * viewModelScope.launch의 확장 형태로, 로딩 상태 처리 및 ceh의 기능을 포함한다.
   * @param hasLoading 로딩 상태를 변경할 지 여부
   * @param action 실행할 코루틴 블록
   * @return Job
   */
  inline fun launch(hasLoading: Boolean = true, crossinline action: suspend CoroutineScope.() -> Unit): Job =
    viewModelScope.launch(ceh) {
      if (hasLoading) setLoading(true)
      action(this)
      if (hasLoading) setLoading(false)
    }

  /**
   * View -> ViewModel로 이벤트를 전달하는 메서드
   * @param event 전달할 이벤트
   */
  fun sendEvent(event: E) {
    viewModelScope.launch {
      _events.emit(event)
    }
  }

  /**
   * ViewModel 내부에서 상태(State)를 갱신하기 위해 Reduce를 전달하는 메서드
   * @param reduce 갱신할 Reduce
   */
  protected fun updateState(reduce: R) {
    viewModelScope.launch {
      _reduce.emit(reduce)
    }
  }

  /**
   * ViewModel 내부에서 SideEffect를 전달하는 메서드
   * @param effect 전달할 SideEffect
   */
  protected fun sendSideEffect(effect: SE) {
    viewModelScope.launch {
      _sideEffect.send(effect)
    }
  }

  /**
   * 수신한 이벤트(Event)를 처리하는 메서드
   * @param event 수신한 이벤트(Event)
   */
  abstract fun handleEvents(event: E)

  /**
   * Reduce 요청에 따라 상태를 변경하는 메서드
   *
   * copy()를 사용한다.
   * @param state 현재 상태
   * @param reduce 수신한 Reduce
   * @return 변경된 상태
   */
  abstract fun reduceState(state: S, reduce: R): S

  /**
   * 수신한 에러를 처리하는 메서드
   * @param error 발생한 에러
   */
  abstract fun handleErrors(error: Throwable)

  companion object {
    const val STATE_KEY = "viewState"
  }
}