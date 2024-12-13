package com.jwd.lunchvote.presentation.screen.login

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jwd.lunchvote.presentation.R
import com.jwd.lunchvote.presentation.screen.login.LoginContract.LoginEvent
import com.jwd.lunchvote.presentation.screen.login.LoginContract.LoginSideEffect
import com.jwd.lunchvote.presentation.screen.login.LoginContract.LoginState
import com.jwd.lunchvote.presentation.util.LocalSnackbarChannel
import com.jwd.lunchvote.presentation.util.clickableWithoutEffect
import com.jwd.lunchvote.presentation.util.login
import com.jwd.lunchvote.presentation.util.loginWithGoogleCredential
import com.jwd.lunchvote.presentation.widget.Gap
import com.jwd.lunchvote.presentation.widget.KakaoLoginButton
import com.jwd.lunchvote.presentation.widget.LoginButtonSize
import com.jwd.lunchvote.presentation.widget.PasswordField
import com.jwd.lunchvote.presentation.widget.Screen
import com.jwd.lunchvote.presentation.widget.ScreenPreview
import com.jwd.lunchvote.presentation.widget.TextField
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collectLatest
import kr.co.inbody.config.config.EmailConfig
import kr.co.inbody.config.error.LoginError
import timber.log.Timber

@Composable
fun LoginRoute(
  navigateToHome: () -> Unit,
  navigateToEmailVerification: () -> Unit,
  modifier: Modifier = Modifier,
  viewModel: LoginViewModel = hiltViewModel(),
  snackbarChannel: Channel<String> = LocalSnackbarChannel.current,
  context: Context = LocalContext.current
) {
  val state by viewModel.viewState.collectAsStateWithLifecycle()
  val loading by viewModel.isLoading.collectAsStateWithLifecycle()

  LaunchedEffect(viewModel.sideEffect) {
    viewModel.sideEffect.collectLatest {
      when (it) {
        is LoginSideEffect.NavigateToHome -> navigateToHome()
        is LoginSideEffect.NavigateToEmailVerification -> navigateToEmailVerification()
        is LoginSideEffect.LaunchKakaoLogin -> {
          try {
            val oAuthToken = UserApiClient.login(context)
            viewModel.sendEvent(LoginEvent.ProcessKakaoLogin(oAuthToken))
          } catch (error: Throwable) {
            if (error is ClientError && error.reason == ClientErrorCause.Cancelled) {
              viewModel.throwError(LoginError.LoginCanceled)
            } else {
              Timber.e(error)
              viewModel.throwError(LoginError.LoginFailure)
            }
          }
        }
        is LoginSideEffect.LaunchGoogleLogin -> {
          try {
            val credential = loginWithGoogleCredential(context)
            viewModel.sendEvent(LoginEvent.ProcessGoogleLogin(credential))
          } catch (error: GetCredentialCancellationException) {
            viewModel.throwError(LoginError.LoginCanceled)
          } catch (error: Throwable) {
            Timber.e(error)
            viewModel.throwError(LoginError.LoginFailure)
          }
        }
        is LoginSideEffect.ShowSnackbar -> snackbarChannel.send(it.message.asString(context))
      }
    }
  }

  LoginScreen(
    state = state,
    modifier = modifier,
    loading = loading,
    onEvent = viewModel::sendEvent
  )
}

@Composable
private fun LoginScreen(
  state: LoginState,
  modifier: Modifier = Modifier,
  loading: Boolean = false,
  onEvent: (LoginEvent) -> Unit = {}
) {
  Screen(
    modifier = modifier.padding(start = 24.dp, top = 24.dp, bottom = 32.dp, end = 24.dp),
    topAppBar = {
      Image(
        painterResource(R.drawable.bg_login_title),
        contentDescription = "Login Title",
        modifier = Modifier
          .fillMaxWidth()
          .heightIn(max = 320.dp)
      )
    }
  ) {
    LoginFields(
      email = state.email,
      password = state.password,
      onEmailChange = { onEvent(LoginEvent.OnEmailChange(it)) },
      onPasswordChange = { onEvent(LoginEvent.OnPasswordChange(it)) },
      onClickEmailLoginButton = { onEvent(LoginEvent.OnClickEmailLoginButton) },
      loading = loading,
      modifier = Modifier.fillMaxWidth()
    )
    Gap(height = 8.dp)
    RegisterRow(
      onClickRegisterButton = { onEvent(LoginEvent.OnClickRegisterButton) },
      loading = loading,
      modifier = Modifier.fillMaxWidth()
    )
    Gap(minHeight = 64.dp)
    SocialLoginButtons(
      onClickKakaoLoginButton = { onEvent(LoginEvent.OnClickKakaoLoginButton) },
      loading = loading,
      modifier = Modifier
        .height(IntrinsicSize.Max)
        .fillMaxWidth()
    )
  }
}

@Composable
private fun LoginFields(
  email: String,
  password: String,
  onEmailChange: (String) -> Unit,
  onPasswordChange: (String) -> Unit,
  onClickEmailLoginButton: () -> Unit,
  loading: Boolean,
  modifier: Modifier = Modifier
) {
  Column(
    modifier = modifier,
    verticalArrangement = Arrangement.spacedBy(20.dp)
  ) {
    val isValid = EmailConfig.REGEX.matches(email)

    Column(
      modifier = Modifier.fillMaxWidth(),
      verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
      TextField(
        text = email,
        onTextChange = onEmailChange,
        hintText = stringResource(R.string.login_email_hint),
        modifier = Modifier.fillMaxWidth(),
        enabled = loading.not(),
        isError = email.isNotEmpty() && isValid.not(),
        errorMessage = stringResource(R.string.login_email_format_error),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
      )
      PasswordField(
        text = password,
        onTextChange = onPasswordChange,
        hintText = stringResource(R.string.login_password_hint),
        modifier = Modifier.fillMaxWidth(),
        enabled = loading.not(),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done)
      )
    }
    Button(
      onClick = onClickEmailLoginButton,
      modifier = Modifier.fillMaxWidth(),
      enabled = loading.not() && email.isNotEmpty() && password.isNotEmpty() && isValid
    ) {
      Text(text = stringResource(R.string.login_button))
    }
  }
}

@Composable
private fun RegisterRow(
  onClickRegisterButton: () -> Unit,
  loading: Boolean,
  modifier: Modifier = Modifier
) {
  Row(
    modifier = modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.End,
    verticalAlignment = Alignment.CenterVertically
  ) {
    Text(
      text = stringResource(R.string.login_register_guide)
    )
    Text(
      text = stringResource(R.string.login_register_button),
      modifier = Modifier
        .clickableWithoutEffect(loading.not()) { onClickRegisterButton() }
        .padding(start = 8.dp, top = 12.dp, bottom = 12.dp, end = 0.dp),
      color = MaterialTheme.colorScheme.primary,
      textDecoration = TextDecoration.Underline
    )
  }
}

@Composable
private fun SocialLoginButtons(
  onClickKakaoLoginButton: () -> Unit,
  loading: Boolean,
  modifier: Modifier = Modifier
) {
  Column(
    modifier = modifier,
    verticalArrangement = Arrangement.spacedBy(12.dp)
  ) {
    KakaoLoginButton(
      onClick = onClickKakaoLoginButton,
      modifier = Modifier.fillMaxWidth(),
      enabled = loading.not(),
      size = LoginButtonSize.Medium
    )
//    TODO: Google Oauth 등록 후 로그인 활성화
//    GoogleLoginButton(
//      onClick = onClickGoogleLoginButton,
//      modifier = Modifier.fillMaxWidth(),
//      enabled = loading.not(),
//      size = LoginButtonSize.Medium
//    )
  }
}

@Preview
@Composable
private fun Default() {
  ScreenPreview {
    LoginScreen(
      LoginState()
    )
  }
}

@Preview
@Composable
private fun InvalidEmail() {
  ScreenPreview {
    LoginScreen(
      LoginState(
        email = "email"
      )
    )
  }
}

@Preview
@Composable
private fun Valid() {
  ScreenPreview {
    LoginScreen(
      LoginState(
        email = "email@email.com",
        password = "password"
      )
    )
  }
}

@Preview
@Composable
private fun Loading() {
  ScreenPreview {
    LoginScreen(
      LoginState(
        email = "email@email.com",
        password = "password"
      ),
      loading = true
    )
  }
}
