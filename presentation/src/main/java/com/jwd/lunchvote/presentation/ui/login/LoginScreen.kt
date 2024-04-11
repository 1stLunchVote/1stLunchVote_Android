package com.jwd.lunchvote.presentation.ui.login

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
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
import com.jwd.lunchvote.core.ui.theme.LunchVoteTheme
import com.jwd.lunchvote.presentation.R
import com.jwd.lunchvote.presentation.ui.login.LoginContract.LoginEvent
import com.jwd.lunchvote.presentation.ui.login.LoginContract.LoginSideEffect
import com.jwd.lunchvote.presentation.ui.login.LoginContract.LoginState
import com.jwd.lunchvote.presentation.util.login
import com.jwd.lunchvote.presentation.widget.LoadingScreen
import com.jwd.lunchvote.presentation.widget.LunchVoteTextField
import com.kakao.sdk.user.UserApiClient
import kotlinx.coroutines.flow.collectLatest

@Composable
fun LoginRoute(
  navigateToHome: () -> Unit,
  navigateToRegisterEmail: () -> Unit,
  showSnackBar: suspend (String) -> Unit,
  modifier: Modifier = Modifier,
  viewModel: LoginViewModel = hiltViewModel(),
  context: Context = LocalContext.current,
) {
  val loginState by viewModel.viewState.collectAsStateWithLifecycle()
  val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()

//    val googleSignInClient by lazy {
//      val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//        .requestIdToken(context.getString(R.string.default_web_client_id))
//        .requestEmail()
//        .build()
//      GoogleSignIn.getClient(context, gso)
//    }
//
//  val googleLauncher =
//    rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
//      if (it.resultCode == Activity.RESULT_OK) {
//        val account = GoogleSignIn.getSignedInAccountFromIntent(it.data)
//        account.result?.let { res ->
//          viewModel.sendEvent(LoginEvent.ProcessGoogleLogin(res))
//        }
//      } else {
//        viewModel.sendEvent(
//          LoginEvent.OnLoginFailure(it.resultCode == Activity.RESULT_CANCELED)
//        )
//      }
//    }

  LaunchedEffect(viewModel.sideEffect) {
    viewModel.sideEffect.collectLatest {
      when (it) {
        is LoginSideEffect.NavigateToHome -> navigateToHome()
        is LoginSideEffect.NavigateToRegisterEmail -> navigateToRegisterEmail()
        is LoginSideEffect.LaunchKakaoLogin -> {
          val oAuthToken = UserApiClient.login(context)
          viewModel.sendEvent(LoginEvent.ProcessKakaoLogin(oAuthToken.accessToken))
        }
        is LoginSideEffect.LaunchGoogleLogin -> { /*googleLauncher.launch(googleSignInClient.signInIntent)*/ }
        is LoginSideEffect.ShowSnackBar -> showSnackBar(it.message.asString(context))
      }
    }
  }

  if (isLoading) LoadingScreen()
  else LoginScreen(
    loginState = loginState,
    modifier = modifier,
    onEmailChanged = { viewModel.sendEvent(LoginEvent.OnChangeEmail(it)) },
    onPasswordChanged = { viewModel.sendEvent(LoginEvent.OnChangePassword(it)) },
    onClickEmailLoginButton = { viewModel.sendEvent(LoginEvent.OnClickEmailLoginButton) },
    onClickRegisterButton = { viewModel.sendEvent(LoginEvent.OnClickRegisterButton) },
    onClickKakaoLoginButton = { viewModel.sendEvent(LoginEvent.OnClickKakaoLoginButton) },
    onClickGoogleLoginButton = { viewModel.sendEvent(LoginEvent.OnClickGoogleLoginButton) }
  )
}

@Composable
private fun LoginScreen(
  loginState: LoginState,
  modifier: Modifier = Modifier,
  onEmailChanged: (String) -> Unit = {},
  onPasswordChanged: (String) -> Unit = {},
  onClickEmailLoginButton: () -> Unit = {},
  onClickRegisterButton: () -> Unit = {},
  onClickKakaoLoginButton: () -> Unit = {},
  onClickGoogleLoginButton: () -> Unit = {}
) {
  Column(
    modifier = modifier
      .fillMaxSize()
      .verticalScroll(rememberScrollState())
  ) {
    Image(
      painter = painterResource(id = R.drawable.bg_login_title),
      contentDescription = "Login Title",
      modifier = Modifier.fillMaxWidth()
    )
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 24.dp)
    ) {
      LoginFields(
        email = loginState.email,
        password = loginState.password,
        onEmailChanged = onEmailChanged,
        onPasswordChanged = onPasswordChanged,
        modifier = Modifier
          .fillMaxWidth()
          .padding(top = 24.dp)
      )
      RegisterRow(
        onClickRegisterButton = onClickRegisterButton,
        modifier = Modifier.fillMaxWidth()
      )
      LoginButtonList(
        onClickEmailLoginButton = onClickEmailLoginButton,
        onClickKakaoLoginButton = onClickKakaoLoginButton,
        onClickGoogleLoginButton = onClickGoogleLoginButton,
        modifier = Modifier.fillMaxWidth()
      )
    }
  }
}

@Composable
private fun LoginFields(
  email: String,
  password: String,
  onEmailChanged: (String) -> Unit,
  onPasswordChanged: (String) -> Unit,
  modifier: Modifier = Modifier,
) {
  Column(
    modifier = modifier,
    verticalArrangement = Arrangement.spacedBy(10.dp)
  ) {
    LunchVoteTextField(
      text = email,
      hintText = stringResource(id = R.string.login_email_hint),
      onTextChanged = onEmailChanged,
      modifier = Modifier.fillMaxWidth(),
      keyboardOptions = KeyboardOptions(
        imeAction = ImeAction.Next
      ),
      keyboardEnterNext = true
    )
    LunchVoteTextField(
      text = password,
      hintText = stringResource(id = R.string.login_password_hint),
      onTextChanged = onPasswordChanged,
      keyboardOptions = KeyboardOptions(
        imeAction = ImeAction.Done,
      ),
      visualTransformation = PasswordVisualTransformation(),
      modifier = Modifier.fillMaxWidth()
    )
  }
}

@Composable
private fun RegisterRow(
  onClickRegisterButton: () -> Unit,
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
    TextButton(
      onClick = onClickRegisterButton,
      contentPadding = PaddingValues(start = 4.dp, top = 12.dp, bottom = 12.dp, end = 0.dp)
    ) {
      Text(
        text = stringResource(R.string.login_register_btn),
        style = MaterialTheme.typography.bodyLarge.copy(
          textDecoration = TextDecoration.Underline
        )
      )
    }
  }
}

@Composable
private fun LoginButtonList(
  onClickEmailLoginButton: () -> Unit,
  onClickKakaoLoginButton: () -> Unit,
  onClickGoogleLoginButton: () -> Unit,
  modifier: Modifier = Modifier
) {
  Column(
    modifier = modifier
  ) {
    Button(
      onClick = onClickEmailLoginButton,
      modifier = Modifier.fillMaxWidth()
    ) {
      Text(stringResource(R.string.login_btn))
    }
    Spacer(modifier = Modifier.height(84.dp))
    Image(
      painter = painterResource(id = R.drawable.bg_kakao_login),
      contentDescription = "Kakao Login",
      contentScale = ContentScale.Fit,
      modifier = Modifier
        .fillMaxWidth()
        .clickable(onClick = onClickKakaoLoginButton)
    )
    Spacer(modifier = Modifier.height(12.dp))
    Image(
      painter = painterResource(id = R.drawable.bg_google_login),
      contentDescription = "Google Login",
      contentScale = ContentScale.Fit,
      modifier = Modifier
        .fillMaxWidth()
        .clickable(onClick = onClickGoogleLoginButton)
    )
  }
}

@Preview
@Composable
private fun LoginScreenPreview() {
  LunchVoteTheme {
    LoginScreen(
      LoginState()
    )
  }
}
