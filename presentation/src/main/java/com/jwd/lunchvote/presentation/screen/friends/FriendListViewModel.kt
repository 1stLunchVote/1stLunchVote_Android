package com.jwd.lunchvote.presentation.screen.friends

import android.os.Parcelable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.jwd.lunchvote.core.ui.base.BaseStateViewModel
import com.jwd.lunchvote.domain.repository.FriendRepository
import com.jwd.lunchvote.domain.repository.LoungeRepository
import com.jwd.lunchvote.domain.repository.UserRepository
import com.jwd.lunchvote.domain.repository.UserStatusRepository
import com.jwd.lunchvote.presentation.R
import com.jwd.lunchvote.presentation.mapper.asUI
import com.jwd.lunchvote.presentation.model.UserUIModel
import com.jwd.lunchvote.presentation.screen.friends.FriendListContract.FriendListEvent
import com.jwd.lunchvote.presentation.screen.friends.FriendListContract.FriendListReduce
import com.jwd.lunchvote.presentation.screen.friends.FriendListContract.FriendListSideEffect
import com.jwd.lunchvote.presentation.screen.friends.FriendListContract.FriendListState
import com.jwd.lunchvote.presentation.util.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kr.co.inbody.config.error.UserError
import java.time.ZonedDateTime
import javax.inject.Inject

@HiltViewModel
class FriendListViewModel @Inject constructor(
  private val userRepository: UserRepository,
  private val friendRepository: FriendRepository,
  private val userStatusRepository: UserStatusRepository,
  private val loungeRepository: LoungeRepository,
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

  private val userId: String
    get() = Firebase.auth.currentUser?.uid ?: throw UserError.NoSession

  override fun handleEvents(event: FriendListEvent) {
    when(event) {
      is FriendListEvent.ScreenInitialize -> launch { initialize() }

      is FriendListEvent.OnClickBackButton -> sendSideEffect(FriendListSideEffect.PopBackStack)
      is FriendListEvent.OnClickFriendRequestButton -> sendSideEffect(FriendListSideEffect.NavigateToFriendRequest)
      is FriendListEvent.OnClickJoinButton -> launch { joinLounge(event.friendId) }
      is FriendListEvent.OnClickDeleteFriendButton -> launch { deleteFriend(event.friendId) }
      is FriendListEvent.OnClickRequestButton -> sendSideEffect(FriendListSideEffect.OpenRequestDialog)

      // DialogEvents
      is FriendListEvent.OnFriendNameChange -> updateState(FriendListReduce.UpdateFriendName(event.friendName))
      is FriendListEvent.OnClickCancelButtonRequestDialog -> sendSideEffect(FriendListSideEffect.CloseDialog)
      is FriendListEvent.OnClickConfirmButtonRequestDialog -> launch { sendRequest() }
    }
  }

  override fun reduceState(state: FriendListState, reduce: FriendListReduce): FriendListState {
    return when(reduce) {
      is FriendListReduce.UpdateJoinedFriendList -> state.copy(joinedFriendList = reduce.joinedFriendList)
      is FriendListReduce.UpdateOnlineFriendList -> state.copy(onlineFriendList = reduce.onlineFriendList)
      is FriendListReduce.UpdateOfflineFriendList -> state.copy(offlineFriendList = reduce.offlineFriendList)
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
    val joinedFriendList = mutableListOf<UserUIModel>()
    val onlineFriendList = mutableListOf<UserUIModel>()
    val offlineFriendList = mutableListOf<UserUIModel>()

    friendRepository.getFriends(userId).map { friendId ->
      val friend = userRepository.getUserById(friendId).asUI()

      userStatusRepository.getUserStatus(friendId)?.asUI()?.let { status ->
        if (status.loungeId != null) {
          joinedFriendList.add(friend)
        } else if (status.lastOnline != null && status.lastOnline > ZonedDateTime.now().minusHours(1)) {
          onlineFriendList.add(friend)
        } else {
          offlineFriendList.add(friend)
        }
      } ?: offlineFriendList.add(friend)
    }

    updateState(FriendListReduce.UpdateJoinedFriendList(joinedFriendList))
    updateState(FriendListReduce.UpdateOnlineFriendList(onlineFriendList))
    updateState(FriendListReduce.UpdateOfflineFriendList(offlineFriendList))
  }

  private suspend fun joinLounge(friendId: String) {
    val loungeId = userStatusRepository.getUserStatus(friendId)?.loungeId ?: return

    sendSideEffect(FriendListSideEffect.NavigateToLounge(loungeId))
  }

  private suspend fun deleteFriend(friendId: String) {
    friendRepository.deleteFriend(userId, friendId)

    sendSideEffect(FriendListSideEffect.ShowSnackbar(UiText.StringResource(R.string.friend_list_delete_friend_snackbar)))
    initialize()
  }

  private suspend fun sendRequest() {
    val friendName = currentState.friendName ?: return

    sendSideEffect(FriendListSideEffect.CloseDialog)

    updateState(FriendListReduce.UpdateFriendName(null))

    val user = userRepository.getUserById(userId).asUI()
    if (friendName == user.name) {
      sendSideEffect(FriendListSideEffect.ShowSnackbar(UiText.StringResource(R.string.friend_list_self_snackbar)))
      return
    }

    val friend = userRepository.getUserByName(friendName).asUI()
    if (friend in (currentState.onlineFriendList + currentState.offlineFriendList)) {
      sendSideEffect(FriendListSideEffect.ShowSnackbar(UiText.StringResource(R.string.friend_list_already_friend_snackbar)))
      return
    }

    val requestList = friendRepository.getSentFriendRequests(userId) + friendRepository.getReceivedFriendRequests(userId)
    requestList.forEach { request ->
      if ((userId == request.userId && friend.id == request.friendId) || (userId == request.friendId && friend.id == request.userId)) {
        sendSideEffect(FriendListSideEffect.ShowSnackbar(UiText.StringResource(R.string.friend_list_already_request_snackbar)))
        return
      }
    }

    friendRepository.requestFriend(userId, friend.id)

    sendSideEffect(FriendListSideEffect.ShowSnackbar(UiText.StringResource(R.string.friend_list_send_request_snackbar)))
  }
}