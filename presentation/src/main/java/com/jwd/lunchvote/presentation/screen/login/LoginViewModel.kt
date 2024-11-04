package com.jwd.lunchvote.presentation.screen.login

import android.os.Parcelable
import androidx.lifecycle.SavedStateHandle
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuthException
import com.jwd.lunchvote.domain.repository.UserRepository
import com.jwd.lunchvote.domain.usecase.GenerateName
import com.jwd.lunchvote.domain.usecase.SignInWithEmailAndPassword
import com.jwd.lunchvote.domain.usecase.SignInWithGoogleIdToken
import com.jwd.lunchvote.domain.usecase.SignInWithKakaoIdToken
import com.jwd.lunchvote.presentation.R
import com.jwd.lunchvote.presentation.base.BaseStateViewModel
import com.jwd.lunchvote.presentation.mapper.asDomain
import com.jwd.lunchvote.presentation.model.UserUIModel
import com.jwd.lunchvote.presentation.screen.login.LoginContract.LoginEvent
import com.jwd.lunchvote.presentation.screen.login.LoginContract.LoginReduce
import com.jwd.lunchvote.presentation.screen.login.LoginContract.LoginSideEffect
import com.jwd.lunchvote.presentation.screen.login.LoginContract.LoginState
import com.jwd.lunchvote.presentation.util.UiText
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.user.UserApiClient
import dagger.hilt.android.lifecycle.HiltViewModel
import kr.co.inbody.config.config.UserConfig
import kr.co.inbody.config.error.LoginError
import kr.co.inbody.config.error.UserError
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
  private val userRepository: UserRepository,
  private val generateName: GenerateName,
  private val signInWithEmailAndPassword: SignInWithEmailAndPassword,
  private val signInWithKakaoIdToken: SignInWithKakaoIdToken,
  private val signInWithGoogleIdToken: SignInWithGoogleIdToken,
  savedStateHandle: SavedStateHandle,
) : BaseStateViewModel<LoginState, LoginEvent, LoginReduce, LoginSideEffect>(savedStateHandle) {
  override fun createInitialState(savedState: Parcelable?): LoginState {
    return savedState as? LoginState ?: LoginState()
  }

  override fun handleEvents(event: LoginEvent) {
    when (event) {
      is LoginEvent.OnEmailChange -> updateState(LoginReduce.UpdateEmail(event.email))
      is LoginEvent.OnPasswordChange -> updateState(LoginReduce.UpdatePassword(event.password))
      is LoginEvent.OnClickEmailLoginButton -> launch { emailLogin() }
      is LoginEvent.OnClickRegisterButton -> sendSideEffect(LoginSideEffect.NavigateToEmailVerification)
      is LoginEvent.OnClickKakaoLoginButton -> sendSideEffect(LoginSideEffect.LaunchKakaoLogin)
      is LoginEvent.ProcessKakaoLogin -> kakaoLogin(event.oAuthToken)
      is LoginEvent.ProcessGoogleLogin -> launch { googleLogin(event.credential) }
    }
  }

  override fun reduceState(state: LoginState, reduce: LoginReduce): LoginState {
    return when (reduce) {
      is LoginReduce.UpdateEmail -> state.copy(email = reduce.email)
      is LoginReduce.UpdatePassword -> state.copy(password = reduce.password)
    }
  }

  override fun handleErrors(error: Throwable) {
    when (error) {
      is FirebaseAuthException -> {
        when (error.errorCode) {
          "ERROR_USER_NOT_FOUND" -> sendSideEffect(LoginSideEffect.ShowSnackbar(UiText.StringResource(R.string.login_user_not_found_error_snackbar)))
          "ERROR_WRONG_PASSWORD" -> sendSideEffect(LoginSideEffect.ShowSnackbar(UiText.StringResource(R.string.login_wrong_password_error_snackbar)))
          "ERROR_USER_DISABLED" -> sendSideEffect(LoginSideEffect.ShowSnackbar(UiText.StringResource(R.string.login_user_disabled_error_snackbar)))
          else -> sendSideEffect(LoginSideEffect.ShowSnackbar(UiText.ErrorString(error)))
        }
      }
      else -> sendSideEffect(LoginSideEffect.ShowSnackbar(UiText.ErrorString(error)))
    }
  }

  private suspend fun emailLogin() {
    signInWithEmailAndPassword(currentState.email, currentState.password)
    loginSuccess()
  }

  private suspend fun googleLogin(credential: GoogleIdTokenCredential) {
    val userId = signInWithGoogleIdToken(credential.idToken)
    val exists = userRepository.checkEmailExists(credential.id)
    val name = generateName(credential.givenName)
    if (exists.not()) {
      val user = UserUIModel(
        id = userId,
        email = credential.id,
        name = name,
        profileImage = credential.profilePictureUri?.toString() ?: UserConfig.DEFAULT_USER_PROFILE_IMAGE
      )
      userRepository.createUser(user.asDomain())
    }
    loginSuccess()
  }

  private fun kakaoLogin(oAuthToken: OAuthToken) {
    setLoading(true)

    UserApiClient.instance.me { user, error ->
      launch {
        when {
          error != null -> throw error
          user == null -> throw UserError.NoUser
          oAuthToken.idToken == null -> throw LoginError.TokenFailed
          else -> {
            val userId = signInWithKakaoIdToken(oAuthToken.idToken!!)
            val exists = userRepository.checkEmailExists(user.kakaoAccount?.email ?: throw LoginError.NoEmail)
            if (exists.not()) {
              val name = generateName(user.kakaoAccount?.profile?.nickname)
              val newUser = UserUIModel(
                id = userId,
                email = user.kakaoAccount?.email ?: UserConfig.DEFAULT_USER_EMAIL,
                name = name,
                profileImage = user.kakaoAccount?.profile?.thumbnailImageUrl ?: UserConfig.DEFAULT_USER_PROFILE_IMAGE
              )
              userRepository.createUser(newUser.asDomain())
            }
            loginSuccess()
          }
        }
      }
    }
  }

  private fun loginSuccess() {
    sendSideEffect(LoginSideEffect.ShowSnackbar(UiText.StringResource(R.string.login_success_snackbar)))
    sendSideEffect(LoginSideEffect.NavigateToHome)
  }
}