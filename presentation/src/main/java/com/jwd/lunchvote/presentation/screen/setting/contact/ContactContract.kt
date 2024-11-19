package com.jwd.lunchvote.presentation.screen.setting.contact

import android.os.Parcelable
import com.jwd.lunchvote.presentation.base.ViewModelContract
import com.jwd.lunchvote.presentation.model.ContactReplyUIModel
import com.jwd.lunchvote.presentation.model.ContactUIModel
import com.jwd.lunchvote.presentation.util.UiText
import kotlinx.parcelize.Parcelize

class ContactContract {
  @Parcelize
  data class ContactState(
    val contact: ContactUIModel = ContactUIModel(),
    val reply: ContactReplyUIModel? = null,
    val deleteDialogState: DeleteDialogState? = null
  ) : ViewModelContract.State, Parcelable {
    override fun toParcelable(): Parcelable = this
  }

  sealed interface ContactEvent : ViewModelContract.Event {
    data object ScreenInitialize : ContactEvent

    data object OnClickBackButton : ContactEvent
    data object OnClickDeleteButton : ContactEvent
  }

  sealed interface ContactReduce : ViewModelContract.Reduce {
    data class UpdateContact(val contact: ContactUIModel) : ContactReduce
    data class UpdateReply(val reply: ContactReplyUIModel?) : ContactReduce
    data class UpdateDeleteDialogState(val deleteDialogState: DeleteDialogState?) : ContactReduce
  }

  sealed interface ContactSideEffect : ViewModelContract.SideEffect {
    data object PopBackStack : ContactSideEffect
    data class ShowSnackbar(val message: UiText) : ContactSideEffect
  }

  @Parcelize
  data object DeleteDialogState : ViewModelContract.State, Parcelable {
    override fun toParcelable(): Parcelable = this
  }

  sealed interface DeleteDialogEvent : ContactEvent {
    data object OnClickCancelButton : DeleteDialogEvent
    data object OnClickDeleteButton : DeleteDialogEvent
  }
}