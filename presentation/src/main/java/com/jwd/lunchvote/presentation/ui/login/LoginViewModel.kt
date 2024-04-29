package com.jwd.lunchvote.presentation.ui.login

import android.os.Parcelable
import androidx.lifecycle.SavedStateHandle
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.jwd.lunchvote.core.common.error.LoginError
import com.jwd.lunchvote.core.common.error.UnknownError
import com.jwd.lunchvote.core.ui.base.BaseStateViewModel
import com.jwd.lunchvote.domain.usecase.CreateUserUseCase
import com.jwd.lunchvote.domain.usecase.SignInWithGoogleIdToken
import com.jwd.lunchvote.domain.usecase.SignInWithKakaoIdToken
import com.jwd.lunchvote.presentation.R
import com.jwd.lunchvote.presentation.mapper.asDomain
import com.jwd.lunchvote.presentation.model.UserUIModel
import com.jwd.lunchvote.presentation.ui.login.LoginContract.LoginEvent
import com.jwd.lunchvote.presentation.ui.login.LoginContract.LoginReduce
import com.jwd.lunchvote.presentation.ui.login.LoginContract.LoginSideEffect
import com.jwd.lunchvote.presentation.ui.login.LoginContract.LoginState
import com.jwd.lunchvote.presentation.util.UiText
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.user.UserApiClient
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
  savedStateHandle: SavedStateHandle,
  private val signInWithKakaoIdToken: SignInWithKakaoIdToken,
  private val signInWithGoogleIdToken: SignInWithGoogleIdToken,
  private val createUserUseCase: CreateUserUseCase
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
      is LoginEvent.ProcessKakaoLogin -> kakaoLogin(event.oAuthToken)
      is LoginEvent.ProcessGoogleLogin -> launch { googleLogin(event.account) }
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

  private suspend fun googleLogin(account: GoogleSignInAccount) {
    val userId = signInWithGoogleIdToken(account.idToken!!)
    val user = UserUIModel(
      id = userId,
      email = account.email ?: "",
      name = account.givenName ?: "",
      profileImageUrl = account.photoUrl?.toString() ?: ""
    )
    createUserUseCase(user.asDomain())
    loginSuccess()
  }

  private fun kakaoLogin(oAuthToken: OAuthToken) {
    setLoading(true)

    UserApiClient.instance.me { user, error ->
      launch {
        when {
          error != null -> throw error
          user == null -> throw LoginError.NoUser
          oAuthToken.idToken == null -> throw LoginError.TokenFailed
          else -> {
            val userId = signInWithKakaoIdToken(oAuthToken.idToken!!)
            val newUser = UserUIModel(
              id = userId,
              email = user.kakaoAccount?.email ?: "",
              name = user.kakaoAccount?.profile?.nickname ?: "",
              profileImageUrl = user.kakaoAccount?.profile?.thumbnailImageUrl ?: ""
            )
            createUserUseCase(newUser.asDomain())
            loginSuccess()
          }
        }
      }
    }
  }

  private fun loginSuccess() {
    sendSideEffect(LoginSideEffect.ShowSnackBar(UiText.StringResource(R.string.login_success_snackbar)))
    sendSideEffect(LoginSideEffect.NavigateToHome)
  }
}