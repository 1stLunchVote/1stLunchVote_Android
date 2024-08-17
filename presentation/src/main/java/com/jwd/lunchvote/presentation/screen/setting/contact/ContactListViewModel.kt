package com.jwd.lunchvote.presentation.screen.setting.contact

import android.os.Parcelable
import androidx.lifecycle.SavedStateHandle
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.jwd.lunchvote.core.ui.base.BaseStateViewModel
import com.jwd.lunchvote.domain.repository.ContactRepository
import com.jwd.lunchvote.presentation.mapper.asUI
import com.jwd.lunchvote.presentation.screen.setting.contact.ContactListContract.ContactListEvent
import com.jwd.lunchvote.presentation.screen.setting.contact.ContactListContract.ContactListReduce
import com.jwd.lunchvote.presentation.screen.setting.contact.ContactListContract.ContactListSideEffect
import com.jwd.lunchvote.presentation.screen.setting.contact.ContactListContract.ContactListState
import com.jwd.lunchvote.presentation.util.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kr.co.inbody.config.error.UserError
import javax.inject.Inject

@HiltViewModel
class ContactListViewModel @Inject constructor(
  private val contactRepository: ContactRepository,
  savedStateHandle: SavedStateHandle
): BaseStateViewModel<ContactListState, ContactListEvent, ContactListReduce, ContactListSideEffect>(savedStateHandle) {
  override fun createInitialState(savedState: Parcelable?): ContactListState {
    return savedState as? ContactListState ?: ContactListState()
  }

  private val userId: String
    get() = Firebase.auth.currentUser?.uid ?: throw UserError.NoSession

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
      is ContactListReduce.UpdateHasReplyOf -> state.copy(hasReplyOf = reduce.hasReplyOf)
    }
  }

  override fun handleErrors(error: Throwable) {
    sendSideEffect(ContactListSideEffect.ShowSnackbar(UiText.ErrorString(error)))
  }

  private suspend fun initialize() {
    val contactList = contactRepository.getContactList(userId).map { it.asUI() }
    val hasReplyOf = contactList.associateWith { contactRepository.getContactReply(it.id) != null }

    updateState(ContactListReduce.UpdateContactList(contactList))
    updateState(ContactListReduce.UpdateHasReplyOf(hasReplyOf))
  }
}