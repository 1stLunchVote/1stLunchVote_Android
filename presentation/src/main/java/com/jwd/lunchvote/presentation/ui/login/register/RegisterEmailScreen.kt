package com.jwd.lunchvote.presentation.ui.login.register

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jwd.lunchvote.core.common.base.error.UnimplementedError
import com.jwd.lunchvote.core.ui.theme.LunchVoteTheme
import com.jwd.lunchvote.presentation.R
import com.jwd.lunchvote.presentation.ui.login.register.RegisterEmailContract.RegisterEmailEvent
import com.jwd.lunchvote.presentation.ui.login.register.RegisterEmailContract.RegisterEmailSideEffect
import com.jwd.lunchvote.presentation.ui.login.register.RegisterEmailContract.RegisterEmailState
import com.jwd.lunchvote.presentation.widget.LoadingScreen
import com.jwd.lunchvote.presentation.widget.LunchVoteTextField
import kotlinx.coroutines.flow.collectLatest

@Composable
fun RegisterEmailRoute(
  showSnackBar: suspend (String) -> Unit,
  modifier: Modifier = Modifier,
  viewModel: RegisterEmailViewModel = hiltViewModel(),
  context: Context = LocalContext.current
) {
  val emailState by viewModel.viewState.collectAsStateWithLifecycle()
  val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()

  LaunchedEffect(viewModel.sideEffect) {
    viewModel.sideEffect.collectLatest {
      when (it) {
        is RegisterEmailSideEffect.NavigateToRegisterPassword -> throw UnimplementedError
        is RegisterEmailSideEffect.ShowSnackBar -> showSnackBar(it.message.asString(context))
      }
    }
  }

  if (isLoading) LoadingScreen()
  else RegisterEmailScreen(
    emailState = emailState,
    modifier = modifier,
    onChangeEmail = { viewModel.sendEvent(RegisterEmailEvent.OnChangeEmail(it)) },
    onClickConfirm = { viewModel.sendEvent(RegisterEmailEvent.OnClickConfirm) }
  )
}

@Composable
private fun RegisterEmailScreen(
  emailState: RegisterEmailState,
  modifier: Modifier = Modifier,
  onChangeEmail: (String) -> Unit = {},
  onClickConfirm: () -> Unit = {}
) {
  Column(
    modifier = modifier
      .fillMaxSize()
      .padding(horizontal = 24.dp)
  ) {
    Spacer(Modifier.height(194.dp))
    Text(
      text = stringResource(R.string.register_email_title),
      style = MaterialTheme.typography.titleLarge
    )
    Spacer(Modifier.height(32.dp))
    Text(
      text = stringResource(R.string.register_email_content),
      style = MaterialTheme.typography.bodyLarge
    )
    Spacer(Modifier.height(64.dp))
    LunchVoteTextField(
      text = emailState.email,
      hintText = "이메일",
      onTextChanged = onChangeEmail,
      modifier = Modifier.fillMaxWidth()
    )
    Spacer(Modifier.weight(1f))
    Button(
      onClick = onClickConfirm,
      enabled = emailState.email.isNotBlank(),
      modifier = Modifier.align(Alignment.CenterHorizontally)
    ) {
      Text(text = stringResource(R.string.register_email_confirm_btn))
    }
    Spacer(Modifier.height(64.dp))
  }
}

@Preview(showBackground = true)
@Composable
private fun RegisterEmailScreenPreview() {
  LunchVoteTheme {
    RegisterEmailScreen(
      RegisterEmailState(email = "abcd1234@gmail.com")
    )
  }
}