package com.jwd.lunchvote.presentation.ui.login.register.password

import android.os.Parcelable
import com.jwd.lunchvote.core.ui.base.ViewModelContract
import com.jwd.lunchvote.presentation.util.UiText
import kotlinx.parcelize.Parcelize

class PasswordContract {
  @Parcelize
  data class PasswordState(
    val email: String = "",
    val password: String = "",
    val passwordConfirm: String = ""
  ) : ViewModelContract.State, Parcelable {
    override fun toParcelable(): Parcelable = this
  }

  sealed interface PasswordEvent : ViewModelContract.Event {
    data object ScreenInitialize : PasswordEvent

    data class OnPasswordChange(val password: String) : PasswordEvent
    data class OnPasswordConfirmChange(val passwordConfirm: String) : PasswordEvent
    data object OnClickNextButton : PasswordEvent
  }

  sealed interface PasswordReduce : ViewModelContract.Reduce {
    data class UpdateEmail(val email: String) : PasswordReduce
    data class UpdatePassword(val password: String) : PasswordReduce
    data class UpdatePasswordConfirm(val passwordConfirm: String) : PasswordReduce
  }

  sealed interface PasswordSideEffect : ViewModelContract.SideEffect {
    data object NavigateToLogin : PasswordSideEffect
    data class NavigateToNickname(val email: String, val password: String) : PasswordSideEffect
    data class ShowSnackbar(val message: UiText) : PasswordSideEffect
  }
}