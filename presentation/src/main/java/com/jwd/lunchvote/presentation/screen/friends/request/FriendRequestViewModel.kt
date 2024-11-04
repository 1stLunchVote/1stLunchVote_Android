package com.jwd.lunchvote.presentation.screen.friends.request

import android.os.Parcelable
import androidx.lifecycle.SavedStateHandle
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.jwd.lunchvote.domain.repository.FriendRepository
import com.jwd.lunchvote.domain.repository.UserRepository
import com.jwd.lunchvote.presentation.R
import com.jwd.lunchvote.presentation.base.BaseStateViewModel
import com.jwd.lunchvote.presentation.mapper.asUI
import com.jwd.lunchvote.presentation.screen.friends.request.FriendRequestContract.FriendRequestEvent
import com.jwd.lunchvote.presentation.screen.friends.request.FriendRequestContract.FriendRequestReduce
import com.jwd.lunchvote.presentation.screen.friends.request.FriendRequestContract.FriendRequestSideEffect
import com.jwd.lunchvote.presentation.screen.friends.request.FriendRequestContract.FriendRequestState
import com.jwd.lunchvote.presentation.util.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kr.co.inbody.config.error.UserError
import javax.inject.Inject

@HiltViewModel
class FriendRequestViewModel @Inject constructor(
  private val userRepository: UserRepository,
  private val friendRepository: FriendRepository,
  savedStateHandle: SavedStateHandle
): BaseStateViewModel<FriendRequestState, FriendRequestEvent, FriendRequestReduce, FriendRequestSideEffect>(savedStateHandle){
  override fun createInitialState(savedState: Parcelable?): FriendRequestState {
    return savedState as? FriendRequestState ?: FriendRequestState()
  }

  private val userId: String
    get() = Firebase.auth.currentUser?.uid ?: throw UserError.NoSession

  override fun handleEvents(event: FriendRequestEvent) {
    when(event) {
      is FriendRequestEvent.ScreenInitialize -> launch { initialize() }

      is FriendRequestEvent.OnClickBackButton -> sendSideEffect(FriendRequestSideEffect.PopBackStack)
      is FriendRequestEvent.OnClickAcceptRequestButton -> launch { acceptFriend(event.friend.id) }
      is FriendRequestEvent.OnClickRejectRequestButton -> launch { rejectFriend(event.friend.id) }
    }
  }

  override fun reduceState(state: FriendRequestState, reduce: FriendRequestReduce): FriendRequestState {
    return when(reduce) {
      is FriendRequestReduce.UpdateRequestSenderMap -> state.copy(requestSenderMap = reduce.requestSenderMap)
    }
  }

  override fun handleErrors(error: Throwable) {
    when (error) {
      is UserError.NoUser -> sendSideEffect(FriendRequestSideEffect.ShowSnackbar(UiText.StringResource(R.string.friend_request_no_user_error_snackbar)))
      else -> sendSideEffect(FriendRequestSideEffect.ShowSnackbar(UiText.ErrorString(error)))
    }
  }

  private suspend fun initialize() {
    val friendRequestList = friendRepository.getReceivedFriendRequests(userId).map { it.asUI() }
    val requestSenderMap = friendRequestList.associateWith { userRepository.getUserById(it.userId).asUI() }

    updateState(FriendRequestReduce.UpdateRequestSenderMap(requestSenderMap))
  }

  private suspend fun acceptFriend(friendId: String) {
    friendRepository.acceptFriend(friendId)
    initialize()
  }

  private suspend fun rejectFriend(friendId: String) {
    friendRepository.rejectFriend(friendId)
    initialize()
  }
}