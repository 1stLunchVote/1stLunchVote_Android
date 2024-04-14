package com.jwd.lunchvote.presentation.ui.login

import android.os.Parcelable
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.jwd.lunchvote.core.ui.base.ViewModelContract
import com.jwd.lunchvote.presentation.util.UiText
import kotlinx.parcelize.Parcelize

class LoginContract {
  @Parcelize
  data class LoginState(
    val email: String = "",
    val password: String = ""
  ) : ViewModelContract.State, Parcelable {
    override fun toParcelable(): Parcelable = this
  }

  sealed interface LoginEvent : ViewModelContract.Event {
    data class OnEmailChange(val email: String) : LoginEvent
    data class OnPasswordChange(val password: String) : LoginEvent
    data object OnClickEmailLoginButton : LoginEvent
    data object OnClickRegisterButton : LoginEvent
    data object OnClickKakaoLoginButton : LoginEvent
    data object OnClickGoogleLoginButton : LoginEvent
    data class ProcessKakaoLogin(val accessToken: String) : LoginEvent
    data class ProcessGoogleLogin(val account: GoogleSignInAccount) : LoginEvent
  }

  sealed interface LoginReduce : ViewModelContract.Reduce {
    data class UpdateEmail(val email: String) : LoginReduce
    data class UpdatePassword(val password: String) : LoginReduce
  }

  sealed interface LoginSideEffect : ViewModelContract.SideEffect {
    data object NavigateToHome : LoginSideEffect
    data object NavigateToRegisterEmail : LoginSideEffect
    data object LaunchGoogleLogin : LoginSideEffect
    data object LaunchKakaoLogin : LoginSideEffect
    data class ShowSnackBar(val message: UiText) : LoginSideEffect
  }
}