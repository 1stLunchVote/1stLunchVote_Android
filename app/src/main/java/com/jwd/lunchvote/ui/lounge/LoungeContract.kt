package com.jwd.lunchvote.ui.lounge

import android.os.Parcelable
import com.jwd.lunchvote.core.ui.base.ViewModelContract
import com.jwd.lunchvote.model.ChatUIModel
import com.jwd.lunchvote.model.MemberUIModel
import kotlinx.parcelize.Parcelize

class LoungeContract {
    @Parcelize
    data class LoungeState(
        val loungeId: String? = null,
        val isOwner: Boolean = false,
        val memberList: List<MemberUIModel> = emptyList(),
        val chatList: List<ChatUIModel> = emptyList(),
        val currentChat: String = "",
        val isReady: Boolean = false,
        val exitDialogShown: Boolean = false,
        val scrollIndex: Int = 0
    ): ViewModelContract.State, Parcelable {
        override fun toParcelable(): Parcelable = this

        // 대기방 주인을 제외하고 전부 다 레디 or 주인 아닌 경우 레디 한 경우
        val allReady : Boolean = memberList.filter { !it.isOwner }.all { it.isReady }
                || (!isOwner && isReady)
    }

    sealed interface LoungeEvent: ViewModelContract.Event {
        data class OnEditChat(val chat: String) : LoungeEvent
        data object OnSendChat : LoungeEvent
        data object OnReady : LoungeEvent
        data object OnTryExit : LoungeEvent
        data object OnStart : LoungeEvent
        data class OnClickExit(val exit: Boolean) : LoungeEvent
        data object OnClickInvite : LoungeEvent
        data class OnScrolled(val index: Int) : LoungeEvent
    }

    sealed interface LoungeReduce: ViewModelContract.Reduce {
        data class SetIsOwner(val isOwner: Boolean) : LoungeReduce
        data class SetLoungeId(val loungeId: String?) : LoungeReduce
        data class SetMemberList(val memberList: List<MemberUIModel>) : LoungeReduce
        data class SetChatList(val chatList: List<ChatUIModel>) : LoungeReduce
        data class SetCurrentChat(val chat: String) : LoungeReduce
        data class SetExitDialogShown(val shown: Boolean) : LoungeReduce
        data class SetScrollIndex(val index: Int) : LoungeReduce
    }

    sealed interface LoungeSideEffect: ViewModelContract.SideEffect {
        data class ShowSnackBar(val message: String) : LoungeSideEffect
        data class PopBackStack(val message: String) : LoungeSideEffect
        data class NavigateToVote(val loungeId: String) : LoungeSideEffect
        data class CopyToClipboard(val loungeId: String) : LoungeSideEffect
    }

    sealed interface LoungeDialogState: ViewModelContract.DialogState
}