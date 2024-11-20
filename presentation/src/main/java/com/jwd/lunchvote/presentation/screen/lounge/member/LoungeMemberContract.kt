package com.jwd.lunchvote.presentation.screen.lounge.member

import android.os.Parcelable
import com.jwd.lunchvote.presentation.base.ViewModelContract
import com.jwd.lunchvote.presentation.model.MemberUIModel
import com.jwd.lunchvote.presentation.model.UserUIModel
import com.jwd.lunchvote.presentation.util.UiText
import kotlinx.parcelize.Parcelize

class LoungeMemberContract {
  @Parcelize
  data class LoungeMemberState(
    val me: MemberUIModel = MemberUIModel(),
    val member: MemberUIModel = MemberUIModel(),
    val user: UserUIModel = UserUIModel(),

    val exileDialogState: ExileDialogState? = null
  ) : ViewModelContract.State, Parcelable {
    override fun toParcelable(): Parcelable = this
  }

  sealed interface LoungeMemberEvent : ViewModelContract.Event {
    data object ScreenInitialize : LoungeMemberEvent
    data object OnClickBackButton : LoungeMemberEvent
    data object OnClickExileButton : LoungeMemberEvent
  }

  sealed interface LoungeMemberReduce : ViewModelContract.Reduce {
    data class UpdateMe(val me: MemberUIModel) : LoungeMemberReduce
    data class UpdateMember(val member: MemberUIModel) : LoungeMemberReduce
    data class UpdateUser(val user: UserUIModel) : LoungeMemberReduce
    data class UpdateExileDialogState(val exileDialogState: ExileDialogState?) : LoungeMemberReduce
  }

  sealed interface LoungeMemberSideEffect : ViewModelContract.SideEffect {
    data object PopBackStack : LoungeMemberSideEffect
    data class ShowSnackbar(val message: UiText) : LoungeMemberSideEffect
  }

  @Parcelize
  data object ExileDialogState : ViewModelContract.State, Parcelable {
    override fun toParcelable(): Parcelable = this
  }

  sealed interface ExileDialogEvent : LoungeMemberEvent {
    data object OnClickCancelButton : ExileDialogEvent
    data object OnClickExileButton : ExileDialogEvent
  }
}