package com.jwd.lunchvote.presentation.screen.friends.request

import android.os.Parcelable
import com.jwd.lunchvote.presentation.base.ViewModelContract
import com.jwd.lunchvote.presentation.model.FriendUIModel
import com.jwd.lunchvote.presentation.model.UserUIModel
import com.jwd.lunchvote.presentation.util.UiText
import kotlinx.parcelize.Parcelize

class FriendRequestContract {
  @Parcelize
  data class FriendRequestState(
    val requestSenderMap: Map<FriendUIModel, UserUIModel> = emptyMap(),

    val rejectDialogState: RejectDialogState? = null
  ) : ViewModelContract.State, Parcelable

  sealed interface FriendRequestEvent : ViewModelContract.Event {
    data object ScreenInitialize : FriendRequestEvent

    data object OnClickBackButton : FriendRequestEvent
    data class OnClickAcceptRequestButton(val requestId: String) : FriendRequestEvent
    data class OnClickRejectRequestButton(val requestId: String, val friendName: String) : FriendRequestEvent
  }

  sealed interface FriendRequestReduce : ViewModelContract.Reduce {
    data class UpdateRequestSenderMap(val requestSenderMap: Map<FriendUIModel, UserUIModel>) : FriendRequestReduce
    data class UpdateRejectDialogState(val rejectDialogState: RejectDialogState?) : FriendRequestReduce
  }

  sealed interface FriendRequestSideEffect : ViewModelContract.SideEffect {
    data object PopBackStack : FriendRequestSideEffect
    data class ShowSnackbar(val message: UiText) : FriendRequestSideEffect
  }

  @Parcelize
  data class RejectDialogState(
    val requestId: String = "",
    val friendName: String = ""
  ) : ViewModelContract.State, Parcelable

  sealed interface RejectDialogEvent : FriendRequestEvent {
    data object OnClickCancelButton : RejectDialogEvent
    data object OnClickRejectButton : RejectDialogEvent
  }
}