package com.jwd.lunchvote.presentation.screen.setting.contact

import android.os.Parcelable
import androidx.lifecycle.SavedStateHandle
import com.jwd.lunchvote.core.ui.base.BaseStateViewModel
import com.jwd.lunchvote.presentation.screen.setting.contact.ContactListContract.ContactListEvent
import com.jwd.lunchvote.presentation.screen.setting.contact.ContactListContract.ContactListReduce
import com.jwd.lunchvote.presentation.screen.setting.contact.ContactListContract.ContactListSideEffect
import com.jwd.lunchvote.presentation.screen.setting.contact.ContactListContract.ContactListState
import com.jwd.lunchvote.presentation.util.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ContactListViewModel @Inject constructor(
  savedStateHandle: SavedStateHandle
): BaseStateViewModel<ContactListState, ContactListEvent, ContactListReduce, ContactListSideEffect>(savedStateHandle) {
  override fun createInitialState(savedState: Parcelable?): ContactListState {
    return savedState as? ContactListState ?: ContactListState()
  }

  override fun handleEvents(event: ContactListEvent) {
    when(event) {
      is ContactListEvent.ScreenInitialize -> launch { initialize() }

      is ContactListEvent.OnClickBackButton -> sendSideEffect(ContactListSideEffect.PopBackStack)
      is ContactListEvent.OnClickContact -> sendSideEffect(ContactListSideEffect.NavigateToContact(event.contact))
    }
  }

  override fun reduceState(state: ContactListState, reduce: ContactListReduce): ContactListState {
    return when (reduce) {
      is ContactListReduce.UpdateContactList -> state.copy(contactList = reduce.contactList)
    }
  }

  override fun handleErrors(error: Throwable) {
    sendSideEffect(ContactListSideEffect.ShowSnackbar(UiText.ErrorString(error)))
  }

  private suspend fun initialize() {

  }
}