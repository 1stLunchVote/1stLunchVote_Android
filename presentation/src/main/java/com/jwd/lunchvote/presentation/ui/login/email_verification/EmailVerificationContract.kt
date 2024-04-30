package com.jwd.lunchvote.presentation.ui.login.email_verification

import android.os.Parcelable
import com.jwd.lunchvote.core.ui.base.ViewModelContract
import com.jwd.lunchvote.presentation.util.UiText
import kotlinx.parcelize.Parcelize

class EmailVerificationContract {
  @Parcelize
  data class EmailVerificationState(
    val email: String = "",
    val emailSent: Boolean = false,
    val code: String = "",
    val isWrongCode: Boolean = false
  ) : ViewModelContract.State, Parcelable {
    override fun toParcelable(): Parcelable = this
  }

  sealed interface EmailVerificationEvent : ViewModelContract.Event {
    data class OnEmailChanged(val email: String) : EmailVerificationEvent
    data object OnClickSendButton : EmailVerificationEvent
    data object OnClickResendButton : EmailVerificationEvent
    data class OnCodeChanged(val code: String) : EmailVerificationEvent
    data object OnClickNextButton : EmailVerificationEvent
  }

  sealed interface EmailVerificationReduce : ViewModelContract.Reduce {
    data class UpdateEmail(val email: String) : EmailVerificationReduce
    data class UpdateEmailSent(val emailSent: Boolean) : EmailVerificationReduce
    data class UpdateCode(val code: String) : EmailVerificationReduce
    data class UpdateIsWrongCode(val isWrongCode: Boolean) : EmailVerificationReduce
  }

  sealed interface EmailVerificationSideEffect : ViewModelContract.SideEffect {
    data object PopBackStack : EmailVerificationSideEffect
    data object OpenQuitDialog : EmailVerificationSideEffect
    data object NavigateToPassword : EmailVerificationSideEffect
    data class ShowSnackBar(val message: UiText) : EmailVerificationSideEffect
  }

  companion object {
    const val QUIT_DIALOG = "quit_dialog"
  }
}