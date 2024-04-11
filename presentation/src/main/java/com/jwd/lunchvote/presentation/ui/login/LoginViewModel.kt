package com.jwd.lunchvote.presentation.ui.login

import android.os.Parcelable
import androidx.lifecycle.SavedStateHandle
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.jwd.lunchvote.core.common.base.error.LoginError
import com.jwd.lunchvote.core.common.base.error.UnknownError
import com.jwd.lunchvote.core.ui.base.BaseStateViewModel
import com.jwd.lunchvote.domain.usecase.login.KakaoLoginUseCase
import com.jwd.lunchvote.presentation.ui.login.LoginContract.LoginEvent
import com.jwd.lunchvote.presentation.ui.login.LoginContract.LoginReduce
import com.jwd.lunchvote.presentation.ui.login.LoginContract.LoginSideEffect
import com.jwd.lunchvote.presentation.ui.login.LoginContract.LoginState
import com.jwd.lunchvote.presentation.util.UiText
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
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
      is LoginEvent.OnChangeEmail -> updateState(LoginReduce.UpdateEmail(event.email))
      is LoginEvent.OnChangePassword -> updateState(LoginReduce.UpdatePassword(event.password))
      is LoginEvent.OnClickEmailLoginButton -> throw NotImplementedError()
      is LoginEvent.OnClickRegisterButton -> sendSideEffect(LoginSideEffect.NavigateToRegisterEmail)
      is LoginEvent.OnClickKakaoLoginButton -> {
        setLoading(true)
        sendSideEffect(LoginSideEffect.LaunchKakaoLogin)
      }
      is LoginEvent.OnClickGoogleLoginButton -> {
        setLoading(true)
        sendSideEffect(LoginSideEffect.LaunchGoogleLogin)
      }
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
    when (error) {
      is ClientError -> if (error.reason == ClientErrorCause.Cancelled) throw LoginError.LoginCanceled
    }
  }

  private fun googleLogin(account: GoogleSignInAccount) {
    val credential = GoogleAuthProvider.getCredential(account.idToken, null)

    auth.signInWithCredential(credential).addOnCompleteListener { task ->
      if (task.isSuccessful) {
        sendSideEffect(LoginSideEffect.ShowSnackBar(UiText.DynamicString("로그인에 성공했습니다.")))
        sendSideEffect(LoginSideEffect.NavigateToHome)
      } else {
        throw LoginError.LoginFailure
      }
    }
  }

  private fun kakaoLogin(accessToken: String) {
    UserApiClient.instance.me { user, error ->
      when {
        error != null -> throw error
        user == null -> throw LoginError.NoUser
        else -> launch {
          runCatching {
            kakaoLoginUseCase(accessToken)
          }.onSuccess {
            sendSideEffect(LoginSideEffect.ShowSnackBar(UiText.DynamicString("로그인에 성공했습니다.")))
            sendSideEffect(LoginSideEffect.NavigateToHome)
          }.onFailure {
            throw LoginError.LoginFailure
          }
        }
      }
    }
  }
}