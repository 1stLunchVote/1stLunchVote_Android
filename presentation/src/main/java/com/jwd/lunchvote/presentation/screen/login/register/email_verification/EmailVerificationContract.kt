package com.jwd.lunchvote.presentation.screen.login.register.email_verification

import android.os.Parcelable
import com.jwd.lunchvote.presentation.base.ViewModelContract
import com.jwd.lunchvote.presentation.util.UiText
import kotlinx.parcelize.Parcelize

class EmailVerificationContract {
  @Parcelize
  data class EmailVerificationState(
    val email: String = "",
    val emailSent: Boolean = false
  ) : ViewModelContract.State, Parcelable {
    override fun toParcelable(): Parcelable = this
  }

  sealed interface EmailVerificationEvent : ViewModelContract.Event {
    data class OnEmailChange(val email: String) : EmailVerificationEvent
    data object OnClickSendButton : EmailVerificationEvent
    data object OnClickResendButton : EmailVerificationEvent
  }

  sealed interface EmailVerificationReduce : ViewModelContract.Reduce {
    data class UpdateEmail(val email: String) : EmailVerificationReduce
    data class UpdateEmailSent(val emailSent: Boolean) : EmailVerificationReduce
  }

  sealed interface EmailVerificationSideEffect : ViewModelContract.SideEffect {
    data class ShowSnackbar(val message: UiText) : EmailVerificationSideEffect
  }
}