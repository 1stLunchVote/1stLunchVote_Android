package com.jwd.lunchvote.presentation.screen.friends

import android.os.Parcelable
import com.jwd.lunchvote.presentation.base.ViewModelContract
import com.jwd.lunchvote.presentation.model.UserUIModel
import com.jwd.lunchvote.presentation.util.UiText
import kotlinx.parcelize.Parcelize

class FriendListContract {
  @Parcelize
  data class FriendListState(
    val joinedFriendList: List<UserUIModel> = emptyList(),
    val onlineFriendList: List<UserUIModel> = emptyList(),
    val offlineFriendList: List<UserUIModel> = emptyList(),

    val requestDialogState: RequestDialogState? = null
  ) : ViewModelContract.State, Parcelable

  sealed interface FriendListEvent : ViewModelContract.Event {
    data object ScreenInitialize : FriendListEvent

    data object OnClickBackButton : FriendListEvent
    data object OnClickFriendRequestButton : FriendListEvent
    data class OnClickJoinButton (val friendId: String) : FriendListEvent
    data class OnClickDeleteFriendButton(val friendId: String) : FriendListEvent
    data object OnClickRequestButton : FriendListEvent
  }

  sealed interface FriendListReduce : ViewModelContract.Reduce {
    data class UpdateJoinedFriendList(val joinedFriendList: List<UserUIModel>) : FriendListReduce
    data class UpdateOnlineFriendList(val onlineFriendList: List<UserUIModel>) : FriendListReduce
    data class UpdateOfflineFriendList(val offlineFriendList: List<UserUIModel>) : FriendListReduce
    data class UpdateRequestDialogState(val requestDialogState: RequestDialogState?) : FriendListReduce
  }

  sealed interface FriendListSideEffect : ViewModelContract.SideEffect {
    data object PopBackStack : FriendListSideEffect
    data object NavigateToFriendRequest : FriendListSideEffect
    data class NavigateToLounge(val loungeId: String) : FriendListSideEffect
    data class ShowSnackbar(val message: UiText) : FriendListSideEffect
  }

  @Parcelize
  data class RequestDialogState(
    val friendName: String = ""
  ) : ViewModelContract.State, Parcelable {
    override fun toParcelable(): Parcelable = this
  }

  sealed interface RequestDialogEvent : FriendListEvent {
    data class OnFriendNameChange(val friendName: String) : RequestDialogEvent
    data object OnClickCancelButton : RequestDialogEvent
    data object OnClickRequestButton : RequestDialogEvent
  }

  sealed interface RequestDialogReduce : FriendListReduce {
    data class UpdateFriendName(val friendName: String) : RequestDialogReduce
  }
}