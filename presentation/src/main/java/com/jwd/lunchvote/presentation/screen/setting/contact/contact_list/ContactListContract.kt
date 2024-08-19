package com.jwd.lunchvote.presentation.screen.setting.contact.contact_list

import android.os.Parcelable
import com.jwd.lunchvote.core.ui.base.ViewModelContract
import com.jwd.lunchvote.presentation.model.ContactUIModel
import com.jwd.lunchvote.presentation.util.UiText
import kotlinx.parcelize.Parcelize

class ContactListContract {
  @Parcelize
  data class ContactListState(
    val contactList: List<ContactUIModel> = emptyList(),
    val hasReplyOf: Map<ContactUIModel, Boolean> = emptyMap()
  ): ViewModelContract.State, Parcelable {
    override fun toParcelable(): Parcelable = this
  }

  sealed interface ContactListEvent: ViewModelContract.Event {
    data object ScreenInitialize: ContactListEvent

    data object OnClickBackButton: ContactListEvent
    data object OnClickAddButton: ContactListEvent
    data class OnClickContact(val contact: ContactUIModel): ContactListEvent
  }

  sealed interface ContactListReduce : ViewModelContract.Reduce {
    data class UpdateContactList(val contactList: List<ContactUIModel>) : ContactListReduce
    data class UpdateHasReplyOf(val hasReplyOf: Map<ContactUIModel, Boolean>) : ContactListReduce
  }

  sealed interface ContactListSideEffect: ViewModelContract.SideEffect {
    data object PopBackStack : ContactListSideEffect
    data object NavigateToAddContact : ContactListSideEffect
    data class NavigateToContact(val contact: ContactUIModel) : ContactListSideEffect
    data class ShowSnackbar(val message: UiText) : ContactListSideEffect
  }
}