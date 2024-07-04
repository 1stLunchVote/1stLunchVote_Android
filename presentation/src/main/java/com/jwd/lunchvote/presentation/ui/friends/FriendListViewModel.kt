package com.jwd.lunchvote.presentation.ui.friends

import android.os.Parcelable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.jwd.lunchvote.core.ui.base.BaseStateViewModel
import com.jwd.lunchvote.domain.repository.FriendRepository
import com.jwd.lunchvote.domain.repository.UserRepository
import com.jwd.lunchvote.presentation.R
import com.jwd.lunchvote.presentation.mapper.asUI
import com.jwd.lunchvote.presentation.ui.friends.FriendListContract.FriendListEvent
import com.jwd.lunchvote.presentation.ui.friends.FriendListContract.FriendListReduce
import com.jwd.lunchvote.presentation.ui.friends.FriendListContract.FriendListSideEffect
import com.jwd.lunchvote.presentation.ui.friends.FriendListContract.FriendListState
import com.jwd.lunchvote.presentation.util.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kr.co.inbody.config.error.UserError
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class FriendListViewModel @Inject constructor(
  private val userRepository: UserRepository,
  private val friendRepository: FriendRepository,
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
      is FriendListEvent.OnClickDeleteFriend -> launch { deleteFriend(event.friendId) }
      is FriendListEvent.OnClickRequestButton -> sendSideEffect(FriendListSideEffect.OpenRequestDialog)

      // DialogEvents
      is FriendListEvent.OnFriendNameChange -> updateState(FriendListReduce.UpdateFriendName(event.friendName))
      is FriendListEvent.OnClickCancelButtonRequestDialog -> sendSideEffect(FriendListSideEffect.CloseDialog)
      is FriendListEvent.OnClickConfirmButtonRequestDialog -> launch { sendRequest() }
    }
  }

  override fun reduceState(state: FriendListState, reduce: FriendListReduce): FriendListState {
    return when(reduce) {
      is FriendListReduce.UpdateFriendList -> state.copy(friendList = reduce.friendList)
      is FriendListReduce.UpdateFriendName -> state.copy(friendName = reduce.friendName)
    }
  }

  override fun handleErrors(error: Throwable) {
    when (error) {
      is UserError.NoUser -> sendSideEffect(FriendListSideEffect.ShowSnackbar(UiText.StringResource(R.string.friend_list_no_user_error_snackbar)))
      else -> sendSideEffect(FriendListSideEffect.ShowSnackbar(UiText.ErrorString(error)))
    }
  }

  private suspend fun initialize() {
    val userId = Firebase.auth.currentUser?.uid ?: throw UserError.NoUser
    val friendList = friendRepository.getFriends(userId).map { friendId ->
      userRepository.getUserById(friendId).asUI()
    }

    updateState(FriendListReduce.UpdateFriendList(friendList))
  }

  private suspend fun deleteFriend(friendId: String) {
    val userId = Firebase.auth.currentUser?.uid ?: throw UserError.NoUser

    friendRepository.deleteFriend(userId, friendId)

    initialize()
    sendSideEffect(FriendListSideEffect.ShowSnackbar(UiText.StringResource(R.string.friend_list_delete_friend_snackbar)))
  }

  private suspend fun sendRequest() {
    val friendName = currentState.friendName ?: return

    sendSideEffect(FriendListSideEffect.CloseDialog)

    updateState(FriendListReduce.UpdateFriendName(null))

    val userId = Firebase.auth.currentUser?.uid ?: throw UserError.NoUser
    val friend = userRepository.getUserByName(friendName).asUI()
    if (friend in currentState.friendList) {
      sendSideEffect(FriendListSideEffect.ShowSnackbar(UiText.StringResource(R.string.friend_list_already_friend_snackbar)))
      return
    }

    friendRepository.requestFriend(userId, friend.id)

    sendSideEffect(FriendListSideEffect.ShowSnackbar(UiText.StringResource(R.string.friend_list_send_request_snackbar)))
  }
}