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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
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
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import timber.log.Timber

@Composable
fun LoginRoute(
    navigateToHome: () -> Unit,
    navigateToRegisterEmail: () -> Unit,
    viewModel: LoginViewModel = hiltViewModel(),
    context: Context = LocalContext.current,
){
    val loginState by viewModel.viewState.collectAsStateWithLifecycle()

    val snackBarHostState = remember { SnackbarHostState() }

    val googleSignInClient by lazy{
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        GoogleSignIn.getClient(context, gso)
    }

    val kakaoCallback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
        if (error != null) {
            Timber.e(error, "카카오계정으로 로그인 실패")
            viewModel.sendEvent(
                LoginEvent.OnLoginFailure(
                    error is ClientError && error.reason == ClientErrorCause.Cancelled
                )
            )
        } else if (token != null) {
            Timber.i("카카오계정으로 로그인 성공 %s", token.accessToken)

            viewModel.sendEvent(LoginEvent.ProcessKakaoLogin(token.accessToken))
        }
    }

    val googleLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()){
        if (it.resultCode == Activity.RESULT_OK){
            val account = GoogleSignIn.getSignedInAccountFromIntent(it.data)
            account.result?.let { res ->
                viewModel.sendEvent(LoginEvent.ProcessGoogleLogin(res)) }
        } else {
            viewModel.sendEvent(
                LoginEvent.OnLoginFailure(it.resultCode == Activity.RESULT_CANCELED)
            )
        }
    }

    // Todo : 카카오 로그인
    // Kakao Sdk 실행 -> Firebase Functions 호출

    LaunchedEffect(viewModel.sideEffect){
        viewModel.sideEffect.collectLatest {
            when(it){
                is LoginSideEffect.NavigateToHome -> {
                    // Todo : 홈화면으로 이동해야함
                    snackBarHostState.showSnackbar("로그인 성공")
                }
                is LoginSideEffect.LaunchGoogleLogin -> {
                    googleLauncher.launch(googleSignInClient.signInIntent)
                }
                is LoginSideEffect.LaunchKakaoLogin -> {
                    kakaoLogin(
                        context = context, kakaoCallback = kakaoCallback,
                        onKakaoLogin = { token ->
                            viewModel.sendEvent(LoginEvent.ProcessKakaoLogin(token))
                        }
                    )
                }
                is LoginSideEffect.ShowSnackBar -> {
                    snackBarHostState.showSnackbar(it.message)
                }
            }
        }
    }

    LoginScreen(
        loginState = loginState,
        snackBarHostState = snackBarHostState,
        onEmailChanged = { viewModel.sendEvent(LoginEvent.SetEmail(it)) },
        onPasswordChanged = { viewModel.sendEvent(LoginEvent.SetPwd(it)) },
        onClickRegister = navigateToRegisterEmail,
        onGoogleLogin = { viewModel.sendEvent(LoginEvent.OnClickGoogleLogin) },
        onKakaoLogin = { viewModel.sendEvent(LoginEvent.OnClickKakaoLogin) },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LoginScreen(
    snackBarHostState: SnackbarHostState,
    loginState: LoginState,
    onEmailChanged: (String) -> Unit = {},
    onPasswordChanged: (String) -> Unit = {},
    onClickRegister: () -> Unit = {},
    onGoogleLogin: () -> Unit = {},
    onKakaoLogin: () -> Unit = {},
){
    val scrollState = rememberScrollState()

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackBarHostState) },
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
                    isLoading = loginState.isLoading,
                    onKakaoLogin = onKakaoLogin,
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
    isLoading : Boolean = false,
    onEmailLogin: () -> Unit = {},
    onKakaoLogin: () -> Unit = {},
    onGoogleLogin: () -> Unit = {},
){
    Button(onClick = onEmailLogin, modifier = Modifier.fillMaxWidth()) {
        ConstraintLayout(modifier = Modifier.fillMaxWidth()) {
            val (text, icon) = createRefs()
            Text(text = stringResource(id = R.string.login_btn), style = buttonTextStyle,
                modifier = Modifier.constrainAs(text){
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                }
            )
            if (isLoading){
                CircularProgressIndicator(
                    color = Color.White,
                    strokeWidth = 2.dp,
                    modifier = Modifier
                        .size(20.dp)
                        .constrainAs(icon) {
                            end.linkTo(parent.end)
                            top.linkTo(parent.top)
                            bottom.linkTo(parent.bottom)
                        },
                )
            }
        }
    }

    Spacer(modifier = Modifier.height(84.dp))

    Image(
        painter = painterResource(id = R.drawable.bg_kakao_login), 
        contentDescription = "kakao_login",
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onKakaoLogin)
    )

    Spacer(modifier = Modifier.height(12.dp))

    Image(
        painter = painterResource(id = R.drawable.bg_google_login),
        contentDescription = "google_login",
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onGoogleLogin)
    )
}

private fun kakaoLogin(
    context: Context,
    kakaoCallback: (OAuthToken?, Throwable?) -> Unit,
    onKakaoLogin: (String) -> Unit
) {
    val kakaoClient = UserApiClient.instance

    // 카카오톡이 설치되어 있으면 카카오톡으로 로그인, 아니면 카카오계정으로 로그인
    if (kakaoClient.isKakaoTalkLoginAvailable(context)) {
        kakaoClient.loginWithKakaoTalk(context) { token, error ->
            if (error != null) {
                Timber.e(error, "카카오톡으로 로그인 실패")

                // 사용자가 카카오톡 설치 후 디바이스 권한 요청 화면에서 로그인을 취소한 경우,
                // 의도적인 로그인 취소로 보고 카카오계정으로 로그인 시도 없이 로그인 취소로 처리 (예: 뒤로 가기)
                if (error is ClientError && error.reason == ClientErrorCause.Cancelled) {
                    return@loginWithKakaoTalk
                }

                // 카카오톡에 연결된 카카오계정이 없는 경우, 카카오계정으로 로그인 시도
                kakaoClient.loginWithKakaoAccount(context, callback = kakaoCallback)
            } else if (token != null) {
                Timber.i("카카오톡으로 로그인 성공 %s", token.accessToken)
                onKakaoLogin(token.accessToken)
            }
        }
    } else {
        kakaoClient.loginWithKakaoAccount(context, callback = kakaoCallback)
    }
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
            snackBarHostState = SnackbarHostState(),
        )
    }
}
