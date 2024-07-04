package com.jwd.lunchvote.presentation.ui.friends.request

import android.os.Parcelable
import com.jwd.lunchvote.core.ui.base.ViewModelContract
import com.jwd.lunchvote.presentation.model.FriendUIModel
import com.jwd.lunchvote.presentation.model.UserUIModel
import com.jwd.lunchvote.presentation.util.UiText
import kotlinx.parcelize.Parcelize

class FriendRequestContract {
  @Parcelize
  data class FriendRequestState(
    val friendRequestList: List<FriendUIModel> = emptyList(),
    val userById: Map<String, UserUIModel> = emptyMap()
  ) : ViewModelContract.State, Parcelable

  sealed interface FriendRequestEvent : ViewModelContract.Event {
    data object ScreenInitialize : FriendRequestEvent

    data object OnClickBackButton : FriendRequestEvent
    data object OnClickAcceptRequestButton : FriendRequestEvent
    data object OnClickRejectRequestButton : FriendRequestEvent
  }

  sealed interface FriendRequestReduce : ViewModelContract.Reduce {
    data class UpdateFriendRequestList(val friendRequestList: List<FriendUIModel>) : FriendRequestReduce
    data class UpdateUserById(val userById: Map<String, UserUIModel>) : FriendRequestReduce
  }

  sealed interface FriendRequestSideEffect : ViewModelContract.SideEffect {
    data object PopBackStack : FriendRequestSideEffect
    data class ShowSnackbar(val message: UiText) : FriendRequestSideEffect
  }
}