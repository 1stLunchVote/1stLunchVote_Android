package com.jwd.lunchvote.presentation.screen.setting.contact

import android.os.Parcelable
import androidx.lifecycle.SavedStateHandle
import com.jwd.lunchvote.core.ui.base.BaseStateViewModel
import com.jwd.lunchvote.presentation.screen.setting.contact.ContactContract.ContactEvent
import com.jwd.lunchvote.presentation.screen.setting.contact.ContactContract.ContactReduce
import com.jwd.lunchvote.presentation.screen.setting.contact.ContactContract.ContactSideEffect
import com.jwd.lunchvote.presentation.screen.setting.contact.ContactContract.ContactState
import com.jwd.lunchvote.presentation.util.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ContactViewModel @Inject constructor(
  savedStateHandle: SavedStateHandle
): BaseStateViewModel<ContactState, ContactEvent, ContactReduce, ContactSideEffect>(savedStateHandle) {
  override fun createInitialState(savedState: Parcelable?): ContactState {
    return savedState as? ContactState ?: ContactState()
  }

  override fun handleEvents(event: ContactEvent) {
    when(event) {
      is ContactEvent.ScreenInitialize -> Unit

      is ContactEvent.OnClickBackButton -> sendSideEffect(ContactSideEffect.PopBackStack)
    }
  }

  override fun reduceState(state: ContactState, reduce: ContactReduce): ContactState {
    return when (reduce) {
      is ContactReduce.UpdateText -> state.copy(text = reduce.text)
    }
  }

  override fun handleErrors(error: Throwable) {
    sendSideEffect(ContactSideEffect.ShowSnackbar(UiText.ErrorString(error)))
  }
}