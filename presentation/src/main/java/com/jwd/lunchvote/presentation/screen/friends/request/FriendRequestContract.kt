package com.jwd.lunchvote.presentation.screen.friends.request

import android.os.Parcelable
import com.jwd.lunchvote.core.ui.base.ViewModelContract
import com.jwd.lunchvote.presentation.model.FriendUIModel
import com.jwd.lunchvote.presentation.model.UserUIModel
import com.jwd.lunchvote.presentation.util.UiText
import kotlinx.parcelize.Parcelize

class FriendRequestContract {
  @Parcelize
  data class FriendRequestState(
    val requestSenderMap: Map<FriendUIModel, UserUIModel> = emptyMap()
  ) : ViewModelContract.State, Parcelable

  sealed interface FriendRequestEvent : ViewModelContract.Event {
    data object ScreenInitialize : FriendRequestEvent

    data object OnClickBackButton : FriendRequestEvent
    data class OnClickAcceptRequestButton(val friend: FriendUIModel) : FriendRequestEvent
    data class OnClickRejectRequestButton(val friend: FriendUIModel) : FriendRequestEvent
  }

  sealed interface FriendRequestReduce : ViewModelContract.Reduce {
    data class UpdateRequestSenderMap(val requestSenderMap: Map<FriendUIModel, UserUIModel>) : FriendRequestReduce
  }

  sealed interface FriendRequestSideEffect : ViewModelContract.SideEffect {
    data object PopBackStack : FriendRequestSideEffect
    data class ShowSnackbar(val message: UiText) : FriendRequestSideEffect
  }
}