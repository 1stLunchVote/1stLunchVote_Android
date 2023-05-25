package com.jwd.lunchvote.ui.login

import android.app.Activity
import android.content.Context
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.ExperimentalComposeUiApi
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
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.jwd.lunchvote.R
import com.jwd.lunchvote.core.ui.theme.LunchVoteTheme
import com.jwd.lunchvote.core.ui.theme.buttonTextStyle
import kotlinx.coroutines.flow.collectLatest
import com.jwd.lunchvote.ui.login.LoginContract.*
import com.jwd.lunchvote.widget.LunchVoteTextField
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun LoginRoute(
    navigateToHome: () -> Unit,
    navigateToRegisterEmail: () -> Unit,
    viewModel: LoginViewModel = hiltViewModel(),
    context: Context = LocalContext.current,
    scope: CoroutineScope = rememberCoroutineScope(),
){
    val loginState by viewModel.viewState.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }

    val googleSignInClient by lazy{
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        GoogleSignIn.getClient(context, gso)
    }

    val googleLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()){
        if (it.resultCode == Activity.RESULT_OK){
            val account = GoogleSignIn.getSignedInAccountFromIntent(it.data)
            account.result?.let { res ->
                viewModel.sendEvent(LoginEvent.ProcessGoogleLogin(res)) }
        } else {
            scope.launch { snackbarHostState.showSnackbar("로그인 오류") }
        }
    }

    LaunchedEffect(viewModel.sideEffect){
        viewModel.sideEffect.collectLatest {
            when(it){
                is LoginSideEffect.NavigateToHome -> {
                    // Todo : 홈화면으로 이동해야함
                    snackbarHostState.showSnackbar("로그인 성공")
                }
                is LoginSideEffect.LaunchGoogleLogin -> {
                    googleLauncher.launch(googleSignInClient.signInIntent)
                }
                is LoginSideEffect.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(it.message)
                }
            }
        }
    }

    LoginScreen(
        loginState = loginState,
        snackbarHostState = snackbarHostState,
        onEmailChanged = { viewModel.sendEvent(LoginEvent.SetEmail(it)) },
        onPasswordChanged = { viewModel.sendEvent(LoginEvent.SetPwd(it)) },
        onClickRegister = navigateToRegisterEmail,
        onGoogleLogin = { viewModel.sendEvent(LoginEvent.OnClickGoogleLogin) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LoginScreen(
    snackbarHostState: SnackbarHostState,
    loginState: LoginState,
    onEmailChanged: (String) -> Unit = {},
    onPasswordChanged: (String) -> Unit = {},
    onClickRegister: () -> Unit = {},
    onGoogleLogin: () -> Unit = {},
){
    val scrollState = rememberScrollState()

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .scrollable(scrollState, orientation = Orientation.Vertical)
                .padding(paddingValues)
        ) {
            val painter = painterResource(id = R.drawable.bg_login_title)

            Image(
                painter = painter,
                contentDescription = "login_title",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(painter.intrinsicSize.width / painter.intrinsicSize.height),
            )

            Column(modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
            ) {
                LoginFields(
                    loginState = loginState,
                    onEmailChanged = onEmailChanged,
                    onPasswordChanged = onPasswordChanged
                )

                Row(horizontalArrangement = Arrangement.End) {
                    Spacer(modifier = Modifier.weight(1f))

                    Text(
                        text = stringResource(id = R.string.login_register_guide),
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.align(CenterVertically)
                    )
                    TextButton(
                        onClick = onClickRegister,
                        contentPadding = PaddingValues(start = 4.dp, top = 12.dp, bottom = 12.dp, end = 0.dp)
                    ) {
                        Text(
                            text = stringResource(id = R.string.login_register_btn),
                            style = MaterialTheme.typography.bodyLarge.copy(
                                textDecoration = TextDecoration.Underline
                            )
                        )
                    }
                }

                LoginButtonList(
                    onKakaoLogin = {},
                    onGoogleLogin = onGoogleLogin,
                )
            }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun LoginFields(
    loginState: LoginState,
    onEmailChanged: (String) -> Unit = {},
    onPasswordChanged: (String) -> Unit = {},
){
    Spacer(modifier = Modifier.height(24.dp))

    LunchVoteTextField(
        text = loginState.email,
        hintText = stringResource(id = R.string.login_email_hint),
        onTextChanged = onEmailChanged,
        modifier = Modifier.fillMaxWidth(),
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Next
        ),
        keyboardEnterNext = true
    )

    Spacer(modifier = Modifier.height(10.dp))

    LunchVoteTextField(
        text = loginState.password,
        hintText = stringResource(id = R.string.login_password_hint),
        onTextChanged = onPasswordChanged,
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Done,
        ),
        visualTransformation = PasswordVisualTransformation(),
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
private fun LoginButtonList(
    onEmailLogin: () -> Unit = {},
    onKakaoLogin: () -> Unit = {},
    onGoogleLogin: () -> Unit = {},
){
    Button(onClick = onEmailLogin, modifier = Modifier.fillMaxWidth()) {
        Text(text = stringResource(id = R.string.login_btn), style = buttonTextStyle)
    }

    Spacer(modifier = Modifier.height(84.dp))
    Image(
        painter = painterResource(id = R.drawable.bg_google_login),
        contentDescription = "google_login",
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onGoogleLogin)
    )
}

@Preview(showBackground = true)
@Composable
private fun SocialLoginButonsPreview(){
    LunchVoteTheme {
        LoginButtonList()
    }
}

@Preview
@Composable
private fun LoginScreenPreview(){
    LunchVoteTheme {
        LoginScreen(
            loginState = LoginState(),
            snackbarHostState = SnackbarHostState(),
        )
    }
}
