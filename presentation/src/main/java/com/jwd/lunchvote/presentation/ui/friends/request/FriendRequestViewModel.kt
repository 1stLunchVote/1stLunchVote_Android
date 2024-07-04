package com.jwd.lunchvote.presentation.ui.friends.request

import android.os.Parcelable
import androidx.lifecycle.SavedStateHandle
import com.jwd.lunchvote.core.ui.base.BaseStateViewModel
import com.jwd.lunchvote.domain.repository.FriendRepository
import com.jwd.lunchvote.domain.repository.UserRepository
import com.jwd.lunchvote.presentation.R
import com.jwd.lunchvote.presentation.ui.friends.request.FriendRequestContract.FriendRequestEvent
import com.jwd.lunchvote.presentation.ui.friends.request.FriendRequestContract.FriendRequestReduce
import com.jwd.lunchvote.presentation.ui.friends.request.FriendRequestContract.FriendRequestSideEffect
import com.jwd.lunchvote.presentation.ui.friends.request.FriendRequestContract.FriendRequestState
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

  override fun handleEvents(event: FriendRequestEvent) {
    when(event) {
      is FriendRequestEvent.ScreenInitialize -> launch { initialize() }

      is FriendRequestEvent.OnClickBackButton -> sendSideEffect(FriendRequestSideEffect.PopBackStack)
      is FriendRequestEvent.OnClickAcceptRequestButton -> {}
      is FriendRequestEvent.OnClickRejectRequestButton -> {}
    }
  }

  override fun reduceState(state: FriendRequestState, reduce: FriendRequestReduce): FriendRequestState {
    return when(reduce) {
      is FriendRequestReduce.UpdateFriendRequestList -> state.copy(friendRequestList = reduce.friendRequestList)
      is FriendRequestReduce.UpdateUserById -> state.copy(userById = reduce.userById)
    }
  }

  override fun handleErrors(error: Throwable) {
    when (error) {
      is UserError.NoUser -> sendSideEffect(FriendRequestSideEffect.ShowSnackbar(UiText.StringResource(R.string.friend_request_no_user_error_snackbar)))
      else -> sendSideEffect(FriendRequestSideEffect.ShowSnackbar(UiText.ErrorString(error)))
    }
  }

  private suspend fun initialize() {

  }
}