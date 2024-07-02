package com.jwd.lunchvote.presentation.ui.friends

import android.os.Parcelable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.jwd.lunchvote.core.ui.base.BaseStateViewModel
import com.jwd.lunchvote.presentation.ui.friends.FriendListContract.*
import com.jwd.lunchvote.presentation.util.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FriendListViewModel @Inject constructor(
  savedStateHandle: SavedStateHandle
): BaseStateViewModel<FriendListState, FriendListEvent, FriendListReduce, FriendListSideEffect>(savedStateHandle){
  override fun createInitialState(savedState: Parcelable?): FriendListState {
    return savedState as? FriendListState ?: FriendListState()
  }

  private val _dialogState = MutableStateFlow("")
  val dialogState: StateFlow<String> = _dialogState.asStateFlow()
  fun setDialogState(dialogState: String) {
    viewModelScope.launch {
      _dialogState.emit(dialogState)
    }
  }

  override fun handleEvents(event: FriendListEvent) {
    when(event) {
      is FriendListEvent.ScreenInitialize -> launch { initialize() }

      is FriendListEvent.OnClickBackButton -> sendSideEffect(FriendListSideEffect.PopBackStack)
      is FriendListEvent.OnClickFriendRequestButton -> sendSideEffect(FriendListSideEffect.NavigateToFriendRequest)

      // DialogEvents
      is FriendListEvent.OnFriendNameChange -> updateState(FriendListReduce.UpdateFriendName(event.friendName))
      is FriendListEvent.OnClickCancelButtonRequestDialog -> sendSideEffect(FriendListSideEffect.CloseDialog)
      is FriendListEvent.OnClickConfirmButtonRequestDialog -> TODO()
    }
  }

  override fun reduceState(state: FriendListState, reduce: FriendListReduce): FriendListState {
    return when(reduce) {
      is FriendListReduce.UpdateFriendList -> state.copy(friendList = reduce.friendList)
      is FriendListReduce.UpdateFriendName -> state.copy(friendName = reduce.friendName)
    }
  }

  override fun handleErrors(error: Throwable) {
    sendSideEffect(FriendListSideEffect.ShowSnackbar(UiText.ErrorString(error)))
  }

  private suspend fun initialize() {

  }
}