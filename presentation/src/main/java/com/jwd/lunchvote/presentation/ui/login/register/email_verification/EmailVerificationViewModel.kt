package com.jwd.lunchvote.presentation.ui.login.register.email_verification

import android.os.Parcelable
import androidx.lifecycle.SavedStateHandle
import com.google.firebase.auth.actionCodeSettings
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.jwd.lunchvote.core.common.error.UnknownError
import com.jwd.lunchvote.core.ui.base.BaseStateViewModel
import com.jwd.lunchvote.domain.usecase.CheckUserExists
import com.jwd.lunchvote.domain.usecase.SetEmailUseCase
import com.jwd.lunchvote.presentation.R
import com.jwd.lunchvote.presentation.ui.login.register.email_verification.EmailVerificationContract.EmailVerificationEvent
import com.jwd.lunchvote.presentation.ui.login.register.email_verification.EmailVerificationContract.EmailVerificationReduce
import com.jwd.lunchvote.presentation.ui.login.register.email_verification.EmailVerificationContract.EmailVerificationSideEffect
import com.jwd.lunchvote.presentation.ui.login.register.email_verification.EmailVerificationContract.EmailVerificationState
import com.jwd.lunchvote.presentation.util.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class EmailVerificationViewModel @Inject constructor(
  private val checkUserExists: CheckUserExists,
  private val setEmailUseCase: SetEmailUseCase,
  savedStateHandle: SavedStateHandle
): BaseStateViewModel<EmailVerificationState, EmailVerificationEvent, EmailVerificationReduce, EmailVerificationSideEffect>(savedStateHandle) {
  override fun createInitialState(savedState: Parcelable?): EmailVerificationState {
    return savedState as? EmailVerificationState ?: EmailVerificationState()
  }

  override fun handleEvents(event: EmailVerificationEvent) {
    when (event) {
      is EmailVerificationEvent.OnEmailChanged -> updateState(EmailVerificationReduce.UpdateEmail(event.email))
      is EmailVerificationEvent.OnClickSendButton -> launch { checkEmail() }
      is EmailVerificationEvent.OnClickResendButton -> sendEmail()
    }
  }

  override fun reduceState(state: EmailVerificationState, reduce: EmailVerificationReduce): EmailVerificationState {
    return when (reduce) {
      is EmailVerificationReduce.UpdateEmail -> state.copy(email = reduce.email)
      is EmailVerificationReduce.UpdateEmailSent -> state.copy(emailSent = reduce.emailSent)
    }
  }

  override fun handleErrors(error: Throwable) {
    sendSideEffect(EmailVerificationSideEffect.ShowSnackBar(UiText.DynamicString(error.message ?: UnknownError.UNKNOWN)))
  }

  private val actionCodeSettings = actionCodeSettings {
    url = "https://github.com/1stLunchVote/1stLunchVote_Android"
    handleCodeInApp = true
    setAndroidPackageName(
      "com.jwd.lunchvote",
      true, // installIfNotAvailable
      "1", // minimumVersion
    )
  }

  private suspend fun checkEmail() {
    val exists = checkUserExists(currentState.email)
    if (exists) sendSideEffect(EmailVerificationSideEffect.ShowSnackBar(UiText.StringResource(R.string.email_verification_user_collision_error_snackbar)))
    else sendEmail()
  }

  private fun sendEmail() {
    Firebase.auth.sendSignInLinkToEmail(currentState.email, actionCodeSettings)
      .addOnCompleteListener { task ->
        if (task.isSuccessful) {
          setEmailUseCase(currentState.email)

          sendSideEffect(EmailVerificationSideEffect.ShowSnackBar(UiText.StringResource(R.string.email_verification_email_send_snackbar)))
          updateState(EmailVerificationReduce.UpdateEmailSent(true))
        } else {
          sendSideEffect(EmailVerificationSideEffect.ShowSnackBar(UiText.StringResource(R.string.email_verification_email_send_error_snackbar)))
        }
      }
  }
}