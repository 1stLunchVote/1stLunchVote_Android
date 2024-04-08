package com.jwd.lunchvote.presentation.ui.login.register

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jwd.lunchvote.core.ui.theme.LunchVoteTheme
import com.jwd.lunchvote.presentation.R
import com.jwd.lunchvote.presentation.ui.login.register.RegisterEmailContract.RegisterEmailEvent
import com.jwd.lunchvote.presentation.ui.login.register.RegisterEmailContract.RegisterEmailSideEffect
import com.jwd.lunchvote.presentation.ui.login.register.RegisterEmailContract.RegisterEmailState
import com.jwd.lunchvote.presentation.widget.LunchVoteTextField
import kotlinx.coroutines.flow.collectLatest

@Composable
fun RegisterEmailRoute(
    viewModel: RegisterEmailViewModel = hiltViewModel()
){
    val emailState : RegisterEmailState by viewModel.viewState.collectAsStateWithLifecycle()

    val snackBarHostState = remember { SnackbarHostState() }

    LaunchedEffect(viewModel.sideEffect){
        viewModel.sideEffect.collectLatest {
            when(it) {
                is RegisterEmailSideEffect.ShowSnackBar -> {
                    snackBarHostState.showSnackbar(it.message)
                }
                is RegisterEmailSideEffect.NavigateToRegisterPassword ->{

                }
            }
        }
    }

    RegisterEmailScreen(
        emailState = emailState,
        onEmailChanged = { viewModel.sendEvent(RegisterEmailEvent.SetEmail(it)) },
        onClickConfirm = { viewModel.sendEvent(RegisterEmailEvent.OnClickConfirm) },
        snackBarHostState = snackBarHostState
    )
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun RegisterEmailScreen(
    emailState: RegisterEmailState,
    onEmailChanged: (String) -> Unit = {},
    onClickConfirm: () -> Unit = {},
    snackBarHostState: SnackbarHostState
){
    Scaffold(
        snackbarHost = { SnackbarHost(snackBarHostState) },
    ) { paddingValues ->
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
            .padding(paddingValues)
        ) {
            Spacer(modifier = Modifier.height(194.dp))

            Text(
                text = stringResource(id = R.string.register_email_title),
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(modifier = Modifier.height(32.dp))

            Text(text = stringResource(id = R.string.register_email_content),
                style = MaterialTheme.typography.bodyLarge
            )

            Spacer(modifier = Modifier.height(64.dp))

            LunchVoteTextField(
                text = emailState.email,
                hintText = "이메일",
                onTextChanged = onEmailChanged,
                modifier = Modifier
                    .fillMaxWidth()
            )

            Spacer(modifier = Modifier.weight(1f))
            
            Button(onClick = onClickConfirm, enabled =  emailState.email.isNotBlank(),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text(text = stringResource(id = R.string.register_email_confirm_btn))
            }

            Spacer(modifier = Modifier.height(64.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun RegisterEmailScreenPreview() {
    LunchVoteTheme {
        RegisterEmailScreen(
            emailState = RegisterEmailState(email = "abcd1234@gmail.com"),
            snackBarHostState = remember { SnackbarHostState() }
        )
    }
}