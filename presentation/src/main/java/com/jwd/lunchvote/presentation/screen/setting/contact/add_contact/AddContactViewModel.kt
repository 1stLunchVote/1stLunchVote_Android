package com.jwd.lunchvote.presentation.screen.setting.contact.add_contact

import android.os.Parcelable
import androidx.lifecycle.SavedStateHandle
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.jwd.lunchvote.core.ui.base.BaseStateViewModel
import com.jwd.lunchvote.domain.repository.ContactRepository
import com.jwd.lunchvote.presentation.mapper.asDomain
import com.jwd.lunchvote.presentation.model.ContactUIModel
import com.jwd.lunchvote.presentation.screen.setting.contact.add_contact.AddContactContract.AddContactEvent
import com.jwd.lunchvote.presentation.screen.setting.contact.add_contact.AddContactContract.AddContactReduce
import com.jwd.lunchvote.presentation.screen.setting.contact.add_contact.AddContactContract.AddContactSideEffect
import com.jwd.lunchvote.presentation.screen.setting.contact.add_contact.AddContactContract.AddContactState
import com.jwd.lunchvote.presentation.util.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kr.co.inbody.config.error.UserError
import javax.inject.Inject

@HiltViewModel
class AddContactViewModel @Inject constructor(
  private val contactRepository: ContactRepository,
  savedStateHandle: SavedStateHandle
): BaseStateViewModel<AddContactState, AddContactEvent, AddContactReduce, AddContactSideEffect>(savedStateHandle) {
  override fun createInitialState(savedState: Parcelable?): AddContactState {
    return savedState as? AddContactState ?: AddContactState()
  }

  private val userId: String
    get() = Firebase.auth.currentUser?.uid ?: throw UserError.NoSession

  override fun handleEvents(event: AddContactEvent) {
    when(event) {
      is AddContactEvent.OnClickBackButton -> sendSideEffect(AddContactSideEffect.PopBackStack)
      is AddContactEvent.OnTitleChange -> updateState(AddContactReduce.UpdateTitle(event.title))
      is AddContactEvent.OnCategoryChange -> updateState(AddContactReduce.UpdateCategory(event.category))
      is AddContactEvent.OnContentChange -> updateState(AddContactReduce.UpdateContent(event.content))
      is AddContactEvent.OnClickSubmitButton -> launch { submit() }
    }
  }

  override fun reduceState(state: AddContactState, reduce: AddContactReduce): AddContactState {
    return when (reduce) {
      is AddContactReduce.UpdateTitle -> state.copy(title = reduce.title)
      is AddContactReduce.UpdateCategory -> state.copy(category = reduce.category)
      is AddContactReduce.UpdateContent -> state.copy(content = reduce.content)
    }
  }

  override fun handleErrors(error: Throwable) {
    sendSideEffect(AddContactSideEffect.ShowSnackbar(UiText.ErrorString(error)))
  }

  private suspend fun submit() {
    val contact = ContactUIModel(
      userId = userId,
      title = currentState.title,
      category = requireNotNull(currentState.category),
      content = currentState.content
    )

    val contactId = contactRepository.addContact(contact.asDomain())

    sendSideEffect(AddContactSideEffect.ShowSnackbar(UiText.DynamicString("문의가 등록되었습니다.")))
    sendSideEffect(AddContactSideEffect.NavigateToContact(contactId))
  }
}