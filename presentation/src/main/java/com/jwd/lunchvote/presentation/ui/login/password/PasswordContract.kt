package com.jwd.lunchvote.presentation.ui.login.password

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
    data class OnPasswordChanged(val password: String) : PasswordEvent
    data class OnPasswordConfirmChanged(val passwordConfirm: String) : PasswordEvent
    data object OnClickNextButton : PasswordEvent
  }

  sealed interface PasswordReduce : ViewModelContract.Reduce {
    data class UpdateEmail(val email: String) : PasswordReduce
    data class UpdatePassword(val password: String) : PasswordReduce
    data class UpdatePasswordConfirm(val passwordConfirm: String) : PasswordReduce
  }

  sealed interface PasswordSideEffect : ViewModelContract.SideEffect {
    data object NavigateToHome : PasswordSideEffect
    data class NavigateToNickname(val email: String, val password: String) : PasswordSideEffect
    data class ShowSnackBar(val message: UiText) : PasswordSideEffect
  }
}