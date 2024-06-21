package com.jwd.lunchvote.presentation.ui.lounge.member

import android.os.Parcelable
import com.jwd.lunchvote.core.ui.base.ViewModelContract
import com.jwd.lunchvote.presentation.model.MemberUIModel
import com.jwd.lunchvote.presentation.model.UserUIModel
import com.jwd.lunchvote.presentation.util.UiText
import kotlinx.parcelize.Parcelize

class LoungeMemberContract {
  @Parcelize
  data class LoungeMemberState(
    val me: MemberUIModel = MemberUIModel(),
    val member: MemberUIModel = MemberUIModel(),
    val user: UserUIModel = UserUIModel()
  ) : ViewModelContract.State, Parcelable {
    override fun toParcelable(): Parcelable = this
  }

  sealed interface LoungeMemberEvent : ViewModelContract.Event {
    data object ScreenInitialize : LoungeMemberEvent
    data object OnClickBackButton : LoungeMemberEvent
    data object OnClickExileButton : LoungeMemberEvent

    // DialogEvents
    data object OnClickCancelButtonExileConfirmDialog : LoungeMemberEvent
    data object OnClickConfirmButtonExileConfirmDialog : LoungeMemberEvent
  }

  sealed interface LoungeMemberReduce : ViewModelContract.Reduce {
    data class UpdateMe(val me: MemberUIModel) : LoungeMemberReduce
    data class UpdateMember(val member: MemberUIModel) : LoungeMemberReduce
    data class UpdateUser(val user: UserUIModel) : LoungeMemberReduce
  }

  sealed interface LoungeMemberSideEffect : ViewModelContract.SideEffect {
    data object PopBackStack : LoungeMemberSideEffect
    data object OpenExileConfirmDialog : LoungeMemberSideEffect
    data object CloseDialog : LoungeMemberSideEffect
    data class ShowSnackbar(val message: UiText) : LoungeMemberSideEffect
  }

  companion object {
    const val EXILE_CONFIRM_DIALOG = "exile_confirm_dialog"
  }
}