package com.jwd.lunchvote.presentation.ui.login.email_verification

import android.os.Parcelable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.jwd.lunchvote.core.common.error.UnknownError
import com.jwd.lunchvote.core.ui.base.BaseStateViewModel
import com.jwd.lunchvote.presentation.ui.login.email_verification.EmailVerificationContract.EmailVerificationEvent
import com.jwd.lunchvote.presentation.ui.login.email_verification.EmailVerificationContract.EmailVerificationReduce
import com.jwd.lunchvote.presentation.ui.login.email_verification.EmailVerificationContract.EmailVerificationSideEffect
import com.jwd.lunchvote.presentation.ui.login.email_verification.EmailVerificationContract.EmailVerificationState
import com.jwd.lunchvote.presentation.util.UiText
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class EmailVerificationViewModel @Inject constructor(
  savedStateHandle: SavedStateHandle
): BaseStateViewModel<EmailVerificationState, EmailVerificationEvent, EmailVerificationReduce, EmailVerificationSideEffect>(savedStateHandle) {
  override fun createInitialState(savedState: Parcelable?): EmailVerificationState {
    return savedState as? EmailVerificationState ?: EmailVerificationState()
  }

  private val _dialogState = MutableStateFlow("")
  val dialogState: StateFlow<String> = _dialogState.asStateFlow()
  fun setDialogState(dialogState: String) {
    viewModelScope.launch {
      _dialogState.emit(dialogState)
    }
  }

  override fun handleEvents(event: EmailVerificationEvent) {
    when (event) {
      is EmailVerificationEvent.OnEmailChanged -> updateState(EmailVerificationReduce.UpdateEmail(event.email))
      is EmailVerificationEvent.OnClickSendButton -> sendEmail()
      is EmailVerificationEvent.OnClickResendButton -> resendEmail()
      is EmailVerificationEvent.OnCodeChanged -> updateState(EmailVerificationReduce.UpdateCode(event.code))
      is EmailVerificationEvent.OnClickNextButton -> sendSideEffect(EmailVerificationSideEffect.NavigateToPassword)
    }
  }

  override fun reduceState(state: EmailVerificationState, reduce: EmailVerificationReduce): EmailVerificationState {
    return when (reduce) {
      is EmailVerificationReduce.UpdateEmail -> state.copy(email = reduce.email)
      is EmailVerificationReduce.UpdateEmailSent -> state.copy(emailSent = reduce.emailSent)
      is EmailVerificationReduce.UpdateCode -> state.copy(code = reduce.code)
    }
  }

  override fun handleErrors(error: Throwable) {
    sendSideEffect(EmailVerificationSideEffect.ShowSnackBar(UiText.DynamicString(error.message ?: UnknownError.UNKNOWN)))
  }

  private fun sendEmail() {
    // Todo : 이메일 전송
    updateState(EmailVerificationReduce.UpdateEmailSent(true))
  }

  private fun resendEmail() {
    // Todo : 이메일 재전송
  }
}