package com.jwd.lunchvote.ui.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jwd.lunchvote.R
import com.jwd.lunchvote.core.ui.theme.LunchVoteTheme
import kotlinx.coroutines.flow.collectLatest
import com.jwd.lunchvote.ui.login.LoginContract.*
import com.jwd.lunchvote.widget.LunchVoteTextField

@Composable
fun LoginRoute(
    navigateToHome: () -> Unit,
    viewModel: LoginViewModel = hiltViewModel(),
){
    val loginState by viewModel.viewState.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel.sideEffect){
        viewModel.sideEffect.collectLatest {
            when(it){
                is LoginSideEffect.NavigateToHome -> navigateToHome()
            }
        }
    }

    LoginScreen(
        loginState = loginState,
        onEmailChanged = { viewModel.sendEvent(LoginEvent.SetEmail(it)) },
        onPasswordChanged = { viewModel.sendEvent(LoginEvent.SetPwd(it)) },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LoginScreen(
    loginState: LoginState,
    onEmailChanged: (String) -> Unit = {},
    onPasswordChanged: (String) -> Unit = {},
){
    val scrollState = rememberScrollState()

    Scaffold(

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
            }
        }
    }
}

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
            imeAction = ImeAction.Done
        ),
        modifier = Modifier.fillMaxWidth()
    )
}

@Preview
@Composable
private fun LoginScreenPreview(){
    LunchVoteTheme {
        LoginScreen(
            loginState = LoginState()
        )
    }
}
