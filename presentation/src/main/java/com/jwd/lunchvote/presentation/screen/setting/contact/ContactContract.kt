package com.jwd.lunchvote.presentation.screen.setting.contact

import android.os.Parcelable
import com.jwd.lunchvote.core.ui.base.ViewModelContract
import com.jwd.lunchvote.presentation.model.ContactReplyUIModel
import com.jwd.lunchvote.presentation.model.ContactUIModel
import com.jwd.lunchvote.presentation.util.UiText
import kotlinx.parcelize.Parcelize

class ContactContract {
  @Parcelize
  data class ContactState(
    val contact: ContactUIModel = ContactUIModel(),
    val reply: ContactReplyUIModel? = null
  ) : ViewModelContract.State, Parcelable {
    override fun toParcelable(): Parcelable = this
  }

  sealed interface ContactEvent : ViewModelContract.Event {
    data object ScreenInitialize : ContactEvent

    data object OnClickBackButton : ContactEvent
    data object OnClickDeleteButton : ContactEvent

    // DialogEvent
    data object OnClickCancelButtonDeleteDialog : ContactEvent
    data object OnClickDeleteButtonDeleteDialog : ContactEvent
  }

  sealed interface ContactReduce : ViewModelContract.Reduce {
    data class UpdateContact(val contact: ContactUIModel) : ContactReduce
    data class UpdateReply(val reply: ContactReplyUIModel?) : ContactReduce
  }

  sealed interface ContactSideEffect : ViewModelContract.SideEffect {
    data object PopBackStack : ContactSideEffect
    data object OpenDeleteDialog : ContactSideEffect
    data object CloseDialog : ContactSideEffect
    data class ShowSnackbar(val message: UiText) : ContactSideEffect
  }

  companion object {
    const val DELETE_DIALOG = "delete_dialog"
  }
}