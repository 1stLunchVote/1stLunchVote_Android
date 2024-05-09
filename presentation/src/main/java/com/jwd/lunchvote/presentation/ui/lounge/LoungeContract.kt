package com.jwd.lunchvote.presentation.ui.lounge

import android.os.Parcelable
import com.jwd.lunchvote.core.ui.base.ViewModelContract
import com.jwd.lunchvote.presentation.model.LoungeChatUIModel
import com.jwd.lunchvote.presentation.model.LoungeUIModel
import com.jwd.lunchvote.presentation.model.MemberUIModel
import com.jwd.lunchvote.presentation.model.UserUIModel
import com.jwd.lunchvote.presentation.util.UiText
import kotlinx.parcelize.Parcelize

class LoungeContract {
  @Parcelize
  data class LoungeState(
    val user: UserUIModel = UserUIModel(),
    val lounge: LoungeUIModel = LoungeUIModel(),
    val memberList: List<MemberUIModel> = emptyList(),
    val chatList: List<LoungeChatUIModel> = emptyList(),
    val chat: String = "",
    val scrollIndex: Int = 0
  ) : ViewModelContract.State, Parcelable {
    override fun toParcelable(): Parcelable = this
  }

  sealed interface LoungeEvent : ViewModelContract.Event {
    data object OnClickBackButton : LoungeEvent
    data class OnClickMember(val member: MemberUIModel) : LoungeEvent
    data object OnClickInviteButton : LoungeEvent
    data class OnChatChanged(val chat: String) : LoungeEvent
    data object OnClickSendChatButton : LoungeEvent
    data object OnClickReadyButton : LoungeEvent
    data class OnScrolled(val index: Int) : LoungeEvent

    // DialogEvents
    data object OnClickCancelButtonVoteExitDialog : LoungeEvent
    data object OnClickConfirmButtonVoteExitDialog : LoungeEvent
  }

  sealed interface LoungeReduce : ViewModelContract.Reduce {
    data class UpdateUser(val user: UserUIModel) : LoungeReduce
    data class UpdateLounge(val lounge: LoungeUIModel) : LoungeReduce
    data class UpdateMemberList(val memberList: List<MemberUIModel>) : LoungeReduce
    data class UpdateChatList(val chatList: List<LoungeChatUIModel>) : LoungeReduce
    data class UpdateChat(val chat: String) : LoungeReduce
    data class UpdateScrollIndex(val index: Int) : LoungeReduce
  }

  sealed interface LoungeSideEffect : ViewModelContract.SideEffect {
    data object PopBackStack : LoungeSideEffect
    data class NavigateToMember(val member: MemberUIModel, val loungeId: String, val isOwner: Boolean) : LoungeSideEffect
    data class NavigateToVote(val loungeId: String) : LoungeSideEffect
    data object OpenVoteExitDialog : LoungeSideEffect
    data object CloseDialog : LoungeSideEffect
    data class ShowSnackBar(val message: UiText) : LoungeSideEffect
    data class CopyToClipboard(val loungeId: String) : LoungeSideEffect
  }

  companion object {
    const val VOTE_EXIT_DIALOG = "vote_exit_dialog"
  }
}