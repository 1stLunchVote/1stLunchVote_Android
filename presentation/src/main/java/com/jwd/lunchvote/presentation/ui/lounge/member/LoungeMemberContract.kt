package com.jwd.lunchvote.presentation.ui.lounge.member

import android.os.Parcelable
import com.jwd.lunchvote.core.ui.base.ViewModelContract
import com.jwd.lunchvote.presentation.util.UiText
import kotlinx.parcelize.Parcelize

class LoungeMemberContract {
  @Parcelize
  data class LoungeMemberState(
    val memberId: String = "",
    val nickname: String = "",
    val profileUrl: String? = null,
    val isOwner: Boolean = false,
  ) : ViewModelContract.State, Parcelable {
    override fun toParcelable(): Parcelable = this
  }

  sealed interface LoungeMemberEvent : ViewModelContract.Event {
    data object OnClickExile : LoungeMemberEvent
  }

  sealed interface LoungeMemberReduce : ViewModelContract.Reduce {
    data class SetMemberInfo(val memberId: String, val nickname: String, val profileUrl: String?, val isOwner: Boolean) : LoungeMemberReduce
  }

  sealed interface LoungeMemberSideEffect : ViewModelContract.SideEffect {
    data object PopBackStack : LoungeMemberSideEffect
    data class ShowSnackBar(val message: UiText) : LoungeMemberSideEffect
  }
}