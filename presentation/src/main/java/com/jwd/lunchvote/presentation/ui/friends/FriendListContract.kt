package com.jwd.lunchvote.presentation.ui.friends

import android.os.Parcelable
import com.jwd.lunchvote.core.ui.base.ViewModelContract
import com.jwd.lunchvote.presentation.model.UserUIModel
import com.jwd.lunchvote.presentation.util.UiText
import kotlinx.parcelize.Parcelize

class FriendListContract {
  @Parcelize
  data class FriendListState(
    val friendList: List<UserUIModel> = emptyList(),
    val friendName: String? = null
  ) : ViewModelContract.State, Parcelable

  sealed interface FriendListEvent : ViewModelContract.Event {
    data object ScreenInitialize : FriendListEvent

    data object OnClickBackButton : FriendListEvent
    data object OnClickFriendRequestButton : FriendListEvent
    data object OnClickRequestButton : FriendListEvent

    // DialogEvents
    data class OnFriendNameChange(val friendName: String) : FriendListEvent
    data object OnClickCancelButtonRequestDialog : FriendListEvent
    data object OnClickConfirmButtonRequestDialog : FriendListEvent
  }

  sealed interface FriendListReduce : ViewModelContract.Reduce {
    data class UpdateFriendList(val friendList: List<UserUIModel>) : FriendListReduce
    data class UpdateFriendName(val friendName: String?) : FriendListReduce
  }

  sealed interface FriendListSideEffect : ViewModelContract.SideEffect {
    data object PopBackStack : FriendListSideEffect
    data object NavigateToFriendRequest : FriendListSideEffect
    data object OpenRequestDialog : FriendListSideEffect
    data object CloseDialog : FriendListSideEffect
    data class ShowSnackbar(val message: UiText) : FriendListSideEffect
  }

  companion object {
    const val REQUEST_DIALOG = "request_dialog"
  }
}