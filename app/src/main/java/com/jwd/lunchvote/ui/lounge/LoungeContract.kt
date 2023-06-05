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
        val memberList: List<MemberUIModel> = emptyList(),
        val chatList: List<ChatUIModel> = emptyList()
    ): ViewModelContract.State, Parcelable {
        override fun toParcelable(): Parcelable = this
    }

    sealed interface LoungeEvent: ViewModelContract.Event {
    }

    sealed interface LoungeReduce: ViewModelContract.Reduce {
        data class SetLoungeId(val loungeId: String?) : LoungeReduce
        data class SetMemberList(val memberList: List<MemberUIModel>) : LoungeReduce
        // Todo : 채팅 데이터 어떻게 업데이트 할지 논의 필요
        data class SetChatList(val chatList: List<ChatUIModel>) : LoungeReduce
    }

    sealed interface LoungeSideEffect: ViewModelContract.SideEffect {
    }
}