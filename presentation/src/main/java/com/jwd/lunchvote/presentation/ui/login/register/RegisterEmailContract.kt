package com.jwd.lunchvote.presentation.ui.login.register

import android.os.Parcelable
import com.jwd.lunchvote.core.ui.base.ViewModelContract
import com.jwd.lunchvote.presentation.util.UiText
import kotlinx.parcelize.Parcelize

class RegisterEmailContract {
  @Parcelize
  data class RegisterEmailState(
    val email: String = "",
  ) : ViewModelContract.State, Parcelable {
    override fun toParcelable(): Parcelable = this
  }

  sealed interface RegisterEmailEvent : ViewModelContract.Event {
    data class OnChangeEmail(val email: String) : RegisterEmailEvent
    data object OnClickConfirm : RegisterEmailEvent
  }

  sealed interface RegisterEmailReduce : ViewModelContract.Reduce {
    data class UpdateEmail(val email: String) : RegisterEmailReduce
  }

  sealed interface RegisterEmailSideEffect : ViewModelContract.SideEffect {
    data object NavigateToRegisterPassword : RegisterEmailSideEffect
    data class ShowSnackBar(val message: UiText) : RegisterEmailSideEffect
  }
}