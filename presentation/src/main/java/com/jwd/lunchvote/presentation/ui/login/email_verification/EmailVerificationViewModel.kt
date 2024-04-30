package com.jwd.lunchvote.presentation.ui.login.email_verification

import android.os.Parcelable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.actionCodeSettings
import com.jwd.lunchvote.core.common.error.UnknownError
import com.jwd.lunchvote.core.ui.base.BaseStateViewModel
import com.jwd.lunchvote.presentation.R
import com.jwd.lunchvote.presentation.ui.login.email_verification.EmailVerificationContract.EmailVerificationEvent
import com.jwd.lunchvote.presentation.ui.login.email_verification.EmailVerificationContract.EmailVerificationReduce
import com.jwd.lunchvote.presentation.ui.login.email_verification.EmailVerificationContract.EmailVerificationSideEffect
import com.jwd.lunchvote.presentation.ui.login.email_verification.EmailVerificationContract.EmailVerificationState
import com.jwd.lunchvote.presentation.util.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class EmailVerificationViewModel @Inject constructor(
  private val auth: FirebaseAuth,
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
      is EmailVerificationEvent.OnCodeChanged -> {
        updateState(EmailVerificationReduce.UpdateIsWrongCode(false))
        updateState(EmailVerificationReduce.UpdateCode(event.code))
      }
      is EmailVerificationEvent.OnClickNextButton -> checkCode()
    }
  }

  override fun reduceState(state: EmailVerificationState, reduce: EmailVerificationReduce): EmailVerificationState {
    return when (reduce) {
      is EmailVerificationReduce.UpdateEmail -> state.copy(email = reduce.email)
      is EmailVerificationReduce.UpdateEmailSent -> state.copy(emailSent = reduce.emailSent)
      is EmailVerificationReduce.UpdateCode -> state.copy(code = reduce.code)
      is EmailVerificationReduce.UpdateIsWrongCode -> state.copy(isWrongCode = reduce.isWrongCode)
    }
  }

  override fun handleErrors(error: Throwable) {
    sendSideEffect(EmailVerificationSideEffect.ShowSnackBar(UiText.DynamicString(error.message ?: UnknownError.UNKNOWN)))
  }

  private fun sendEmail() {
    val actionCodeSettings = actionCodeSettings {
      url = "https://github.com/1stLunchVote/1stLunchVote_Android"
      handleCodeInApp = true
      setAndroidPackageName(
        "com.jwd.lunchvote",
        true, // installIfNotAvailable
        "1", // minimumVersion
      )
    }

    auth.sendSignInLinkToEmail(currentState.email, actionCodeSettings)
      .addOnCompleteListener { task ->
        if (task.isSuccessful) {
          sendSideEffect(EmailVerificationSideEffect.ShowSnackBar(UiText.StringResource(R.string.email_verification_email_send_snackbar)))
          updateState(EmailVerificationReduce.UpdateEmailSent(true))
        } else {
          sendSideEffect(EmailVerificationSideEffect.ShowSnackBar(UiText.StringResource(R.string.email_verification_email_send_error_snackbar)))
        }
      }
  }

  private fun resendEmail() {
    // Todo : 이메일 재전송
  }

  private fun checkCode() {
    if (currentState.code == "123456") {
      sendSideEffect(EmailVerificationSideEffect.NavigateToPassword)
    } else {
      sendSideEffect(EmailVerificationSideEffect.ShowSnackBar(UiText.StringResource(R.string.email_verification_wrong_code_error_snackbar)))
      updateState(EmailVerificationReduce.UpdateIsWrongCode(true))
    }
  }
}