package com.jwd.lunchvote.presentation.ui.login

import android.os.Parcelable
import androidx.lifecycle.SavedStateHandle
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.jwd.lunchvote.core.common.error.LoginError
import com.jwd.lunchvote.core.common.error.UnknownError
import com.jwd.lunchvote.core.ui.base.BaseStateViewModel
import com.jwd.lunchvote.domain.usecase.KakaoLoginUseCase
import com.jwd.lunchvote.presentation.ui.login.LoginContract.LoginEvent
import com.jwd.lunchvote.presentation.ui.login.LoginContract.LoginReduce
import com.jwd.lunchvote.presentation.ui.login.LoginContract.LoginSideEffect
import com.jwd.lunchvote.presentation.ui.login.LoginContract.LoginState
import com.jwd.lunchvote.presentation.util.UiText
import com.kakao.sdk.user.UserApiClient
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
  savedStateHandle: SavedStateHandle,
  private val auth: FirebaseAuth,
  private val kakaoLoginUseCase: KakaoLoginUseCase
) : BaseStateViewModel<LoginState, LoginEvent, LoginReduce, LoginSideEffect>(savedStateHandle) {
  override fun createInitialState(savedState: Parcelable?): LoginState {
    return savedState as? LoginState ?: LoginState()
  }

  override fun handleEvents(event: LoginEvent) {
    when (event) {
      is LoginEvent.OnEmailChange -> updateState(LoginReduce.UpdateEmail(event.email))
      is LoginEvent.OnPasswordChange -> updateState(LoginReduce.UpdatePassword(event.password))
      is LoginEvent.OnClickEmailLoginButton -> throwError(NotImplementedError())
      is LoginEvent.OnClickRegisterButton -> sendSideEffect(LoginSideEffect.NavigateToRegisterEmail)
      is LoginEvent.OnClickKakaoLoginButton -> sendSideEffect(LoginSideEffect.LaunchKakaoLogin)
      is LoginEvent.OnClickGoogleLoginButton -> sendSideEffect(LoginSideEffect.LaunchGoogleLogin)
      is LoginEvent.ProcessKakaoLogin -> kakaoLogin(event.accessToken)
      is LoginEvent.ProcessGoogleLogin -> googleLogin(event.account)
    }
  }

  override fun reduceState(state: LoginState, reduce: LoginReduce): LoginState {
    return when (reduce) {
      is LoginReduce.UpdateEmail -> state.copy(email = reduce.email)
      is LoginReduce.UpdatePassword -> state.copy(password = reduce.password)
    }
  }

  override fun handleErrors(error: Throwable) {
    sendSideEffect(LoginSideEffect.ShowSnackBar(UiText.DynamicString(error.message ?: UnknownError.UNKNOWN)))
  }

  private fun googleLogin(account: GoogleSignInAccount) {
    val credential = GoogleAuthProvider.getCredential(account.idToken, null)

    auth.signInWithCredential(credential).addOnCompleteListener { task ->
      if (task.isSuccessful) loginSuccess()
      else throwError(LoginError.LoginFailure)
    }
  }

  private fun kakaoLogin(accessToken: String) {
    setLoading(true)

    UserApiClient.instance.me { user, error ->
      launch {
        when {
          error != null -> throw error
          user == null -> throw LoginError.NoUser
          else -> {
            kakaoLoginUseCase(accessToken)
            loginSuccess()
          }
        }
      }
    }
  }

  private fun loginSuccess() {
    sendSideEffect(LoginSideEffect.ShowSnackBar(UiText.DynamicString("로그인에 성공했습니다.")))
    sendSideEffect(LoginSideEffect.NavigateToHome)
  }
}