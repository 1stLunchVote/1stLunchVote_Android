package com.jwd.lunchvote.presentation.screen.setting.contact

import android.os.Parcelable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.jwd.lunchvote.domain.repository.ContactRepository
import com.jwd.lunchvote.presentation.base.BaseStateViewModel
import com.jwd.lunchvote.presentation.mapper.asUI
import com.jwd.lunchvote.presentation.navigation.LunchVoteNavRoute
import com.jwd.lunchvote.presentation.screen.setting.contact.ContactContract.ContactEvent
import com.jwd.lunchvote.presentation.screen.setting.contact.ContactContract.ContactReduce
import com.jwd.lunchvote.presentation.screen.setting.contact.ContactContract.ContactSideEffect
import com.jwd.lunchvote.presentation.screen.setting.contact.ContactContract.ContactState
import com.jwd.lunchvote.presentation.screen.setting.contact.ContactContract.DeleteDialogEvent
import com.jwd.lunchvote.presentation.screen.setting.contact.ContactContract.DeleteDialogState
import com.jwd.lunchvote.presentation.util.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kr.co.inbody.config.error.RouteError
import javax.inject.Inject

@HiltViewModel
class ContactViewModel @Inject constructor(
  private val contactRepository: ContactRepository,
  savedStateHandle: SavedStateHandle
): BaseStateViewModel<ContactState, ContactEvent, ContactReduce, ContactSideEffect>(savedStateHandle) {
  override fun createInitialState(savedState: Parcelable?): ContactState {
    return savedState as? ContactState ?: ContactState()
  }

  private val contactId: String =
    savedStateHandle[LunchVoteNavRoute.Contact.arguments.first().name] ?: throw RouteError.NoArguments

  override fun handleEvents(event: ContactEvent) {
    when(event) {
      is ContactEvent.ScreenInitialize -> launch { initialize() }

      is ContactEvent.OnClickBackButton -> sendSideEffect(ContactSideEffect.PopBackStack)
      is ContactEvent.OnClickDeleteButton -> updateState(ContactReduce.UpdateDeleteDialogState(DeleteDialogState))

      is DeleteDialogEvent -> handleDeleteDialogEvents(event)
    }
  }

  private fun handleDeleteDialogEvents(event: DeleteDialogEvent) {
    when(event) {
      is DeleteDialogEvent.OnClickCancelButton -> updateState(ContactReduce.UpdateDeleteDialogState(null))
      is DeleteDialogEvent.OnClickDeleteButton -> launch { deleteContact() }
    }
  }

  override fun reduceState(state: ContactState, reduce: ContactReduce): ContactState {
    return when (reduce) {
      is ContactReduce.UpdateContact -> state.copy(contact = reduce.contact)
      is ContactReduce.UpdateReply -> state.copy(reply = reduce.reply)
      is ContactReduce.UpdateDeleteDialogState -> state.copy(deleteDialogState = reduce.deleteDialogState)
    }
  }

  override fun handleErrors(error: Throwable) {
    sendSideEffect(ContactSideEffect.ShowSnackbar(UiText.ErrorString(error)))
  }

  private suspend fun initialize() {
    val contact = contactRepository.getContactById(contactId).asUI()
    val reply = contactRepository.getContactReply(contactId)?.asUI()

    updateState(ContactReduce.UpdateContact(contact))
    updateState(ContactReduce.UpdateReply(reply))
  }

  private suspend fun deleteContact() {
    contactRepository.deleteContract(contactId)

    sendSideEffect(ContactSideEffect.ShowSnackbar(UiText.DynamicString("문의가 삭제되었습니다.")))
    sendSideEffect(ContactSideEffect.PopBackStack)
  }
}