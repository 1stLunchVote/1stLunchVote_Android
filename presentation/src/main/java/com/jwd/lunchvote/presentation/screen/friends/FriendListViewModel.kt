package com.jwd.lunchvote.presentation.screen.friends

import android.os.Parcelable
import androidx.lifecycle.SavedStateHandle
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.jwd.lunchvote.domain.repository.FriendRepository
import com.jwd.lunchvote.domain.repository.UserRepository
import com.jwd.lunchvote.domain.repository.UserStatusRepository
import com.jwd.lunchvote.presentation.R
import com.jwd.lunchvote.presentation.base.BaseStateViewModel
import com.jwd.lunchvote.presentation.mapper.asUI
import com.jwd.lunchvote.presentation.model.UserUIModel
import com.jwd.lunchvote.presentation.screen.friends.FriendListContract.FriendListEvent
import com.jwd.lunchvote.presentation.screen.friends.FriendListContract.FriendListReduce
import com.jwd.lunchvote.presentation.screen.friends.FriendListContract.FriendListSideEffect
import com.jwd.lunchvote.presentation.screen.friends.FriendListContract.FriendListState
import com.jwd.lunchvote.presentation.screen.friends.FriendListContract.RequestDialogEvent
import com.jwd.lunchvote.presentation.screen.friends.FriendListContract.RequestDialogReduce
import com.jwd.lunchvote.presentation.screen.friends.FriendListContract.RequestDialogState
import com.jwd.lunchvote.presentation.util.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kr.co.inbody.config.error.UserError
import java.time.ZonedDateTime
import javax.inject.Inject

@HiltViewModel
class FriendListViewModel @Inject constructor(
  private val userRepository: UserRepository,
  private val friendRepository: FriendRepository,
  private val userStatusRepository: UserStatusRepository,
  savedStateHandle: SavedStateHandle
): BaseStateViewModel<FriendListState, FriendListEvent, FriendListReduce, FriendListSideEffect>(savedStateHandle){
  override fun createInitialState(savedState: Parcelable?): FriendListState {
    return savedState as? FriendListState ?: FriendListState()
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
      is FriendListEvent.OnClickRequestButton -> updateState(RequestDialogReduce.OpenDialog)
      is RequestDialogEvent -> handleRequestDialogEvents(event)
    }
  }

  private fun handleRequestDialogEvents(event: RequestDialogEvent) {
    when(event) {
      is RequestDialogEvent.OnFriendNameChange -> updateState(RequestDialogReduce.UpdateFriendName(event.friendName))
      is RequestDialogEvent.OnClickCancelButton -> updateState(RequestDialogReduce.CloseDialog)
      is RequestDialogEvent.OnClickConfirmButton -> launch { sendRequest() }
    }
  }

  override fun reduceState(state: FriendListState, reduce: FriendListReduce): FriendListState {
    return when(reduce) {
      is FriendListReduce.UpdateJoinedFriendList -> state.copy(joinedFriendList = reduce.joinedFriendList)
      is FriendListReduce.UpdateOnlineFriendList -> state.copy(onlineFriendList = reduce.onlineFriendList)
      is FriendListReduce.UpdateOfflineFriendList -> state.copy(offlineFriendList = reduce.offlineFriendList)
      is RequestDialogReduce -> state.copy(requestDialogState = reduceRequestDialogState(state.requestDialogState, reduce))
    }
  }

  private fun reduceRequestDialogState(state: RequestDialogState?, reduce: RequestDialogReduce): RequestDialogState? {
    return when(reduce) {
      is RequestDialogReduce.OpenDialog -> RequestDialogState()
      is RequestDialogReduce.CloseDialog -> null
      is RequestDialogReduce.UpdateFriendName -> state?.copy(friendName = reduce.friendName)
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
    val friendName = currentState.requestDialogState?.friendName ?: return

    updateState(RequestDialogReduce.CloseDialog)

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