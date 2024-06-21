package com.jwd.lunchvote.presentation.ui.login

import android.app.Activity
import android.content.Context
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.jwd.lunchvote.presentation.BuildConfig
import com.jwd.lunchvote.presentation.R
import com.jwd.lunchvote.presentation.ui.login.LoginContract.LoginEvent
import com.jwd.lunchvote.presentation.ui.login.LoginContract.LoginSideEffect
import com.jwd.lunchvote.presentation.ui.login.LoginContract.LoginState
import com.jwd.lunchvote.presentation.util.LocalSnackbarChannel
import com.jwd.lunchvote.presentation.util.clickableWithoutEffect
import com.jwd.lunchvote.presentation.util.login
import com.jwd.lunchvote.presentation.widget.Gap
import com.jwd.lunchvote.presentation.widget.GoogleLoginButton
import com.jwd.lunchvote.presentation.widget.KakaoLoginButton
import com.jwd.lunchvote.presentation.widget.LoginButtonSize
import com.jwd.lunchvote.presentation.widget.LunchVoteTextField
import com.jwd.lunchvote.presentation.widget.Screen
import com.jwd.lunchvote.presentation.widget.ScreenPreview
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collectLatest
import kr.co.inbody.config.config.EmailConfig
import kr.co.inbody.config.error.LoginError

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

  val googleSignInClient by lazy {
    val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
      .requestIdToken(BuildConfig.FIREBASE_WEB_CLIENT_ID)
      .requestEmail()
      .build()
    GoogleSignIn.getClient(context, googleSignInOptions)
  }

  val googleLauncher =
    rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
      when (it.resultCode) {
        Activity.RESULT_OK -> {
          val account = GoogleSignIn.getSignedInAccountFromIntent(it.data)
          account.result?.let { res ->
            viewModel.sendEvent(LoginEvent.ProcessGoogleLogin(res))
          }
        }
        Activity.RESULT_CANCELED -> viewModel.throwError(LoginError.LoginCanceled)
        else -> viewModel.throwError(LoginError.LoginFailure)
      }
    }

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
              viewModel.throwError(LoginError.LoginFailure)
            }
          }
        }
        is LoginSideEffect.LaunchGoogleLogin -> googleLauncher.launch(googleSignInClient.signInIntent)
        is LoginSideEffect.ShowSnackBar -> snackbarChannel.send(it.message.asString(context))
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
    modifier = modifier,
    topAppBar = {
      Image(
        painterResource(R.drawable.bg_login_title),
        contentDescription = "Login Title",
        modifier = Modifier.fillMaxWidth()
      )
    },
    scrollable = false
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 24.dp)
        .padding(top = 24.dp, bottom = 32.dp)
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
      Gap(minHeight = 32.dp)
      SocialLoginButtons(
        onClickKakaoLoginButton = { onEvent(LoginEvent.OnClickKakaoLoginButton) },
        onClickGoogleLoginButton = { onEvent(LoginEvent.OnClickGoogleLoginButton) },
        loading = loading,
        modifier = Modifier.fillMaxWidth()
      )
    }
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
  modifier: Modifier = Modifier,
) {
  Column(
    modifier = modifier,
    verticalArrangement = Arrangement.spacedBy(8.dp)
  ) {
    val isValid = EmailConfig.REGEX.matches(email)

    LunchVoteTextField(
      text = email,
      onTextChange = onEmailChange,
      hintText = stringResource(R.string.login_email_hint),
      modifier = Modifier.fillMaxWidth(),
      enabled = loading.not(),
      keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
    )
    LunchVoteTextField(
      text = password,
      onTextChange = onPasswordChange,
      hintText = stringResource(R.string.login_password_hint),
      modifier = Modifier.fillMaxWidth(),
      enabled = loading.not(),
      keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
      visualTransformation = PasswordVisualTransformation()
    )
    Text(
      text = stringResource(R.string.login_email_format_error),
      modifier = Modifier
        .padding(horizontal = 8.dp)
        .alpha(if (email.isNotEmpty() && isValid.not()) 1f else 0f),
      color = MaterialTheme.colorScheme.error,
      style = MaterialTheme.typography.labelMedium
    )
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
      text = stringResource(R.string.login_register_guide),
      style = MaterialTheme.typography.bodyLarge
    )
    Text(
      text = stringResource(R.string.login_register_button),
      modifier = Modifier
        .clickableWithoutEffect(
          enabled = loading.not(),
          onClick = onClickRegisterButton
        )
        .padding(start = 8.dp, top = 12.dp, bottom = 12.dp, end = 0.dp),
      color = MaterialTheme.colorScheme.primary,
      style = MaterialTheme.typography.bodyLarge.copy(
        textDecoration = TextDecoration.Underline
      )
    )
  }
}

@Composable
private fun SocialLoginButtons(
  onClickKakaoLoginButton: () -> Unit,
  onClickGoogleLoginButton: () -> Unit,
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
    GoogleLoginButton(
      onClick = onClickGoogleLoginButton,
      modifier = Modifier.fillMaxWidth(),
      enabled = loading.not(),
      size = LoginButtonSize.Medium
    )
  }
}

@Preview
@Composable
private fun Preview1() {
  ScreenPreview {
    LoginScreen(
      LoginState()
    )
  }
}

@Preview
@Composable
private fun Preview2() {
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
private fun Preview3() {
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
private fun Preview4() {
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
