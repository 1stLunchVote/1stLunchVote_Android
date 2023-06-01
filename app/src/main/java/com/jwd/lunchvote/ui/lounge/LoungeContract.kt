package com.jwd.lunchvote.ui.lounge

import android.os.Parcelable
import com.jwd.lunchvote.core.ui.base.ViewModelContract
import com.jwd.lunchvote.model.LoungeMember
import kotlinx.parcelize.Parcelize

class LoungeContract {
    @Parcelize
    data class LoungeState(
        val loungeId: String? = null,
        val memberList: List<LoungeMember> = emptyList()
    ): ViewModelContract.State, Parcelable

    sealed interface LoungeEvent: ViewModelContract.Event {
    }

    sealed interface LoungeReduce: ViewModelContract.Reduce {
        data class SetLoungeId(val loungeId: String?) : LoungeReduce
        data class SetMemberList(val memberList: List<LoungeMember>) : LoungeReduce
    }

    sealed interface LoungeSideEffect: ViewModelContract.SideEffect {
    }
}