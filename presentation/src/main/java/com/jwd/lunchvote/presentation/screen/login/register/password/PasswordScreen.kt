package com.jwd.lunchvote.presentation.screen.login.register.password

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jwd.lunchvote.presentation.R
import com.jwd.lunchvote.presentation.screen.login.register.password.PasswordContract.PasswordEvent
import com.jwd.lunchvote.presentation.screen.login.register.password.PasswordContract.PasswordSideEffect
import com.jwd.lunchvote.presentation.screen.login.register.password.PasswordContract.PasswordState
import com.jwd.lunchvote.presentation.util.LocalSnackbarChannel
import com.jwd.lunchvote.presentation.widget.Gap
import com.jwd.lunchvote.presentation.widget.LoadingScreen
import com.jwd.lunchvote.presentation.widget.PasswordField
import com.jwd.lunchvote.presentation.widget.Screen
import com.jwd.lunchvote.presentation.widget.ScreenPreview
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collectLatest

@Composable
fun PasswordRoute(
  navigateToLogin: () -> Unit,
  navigateToNickname: (String, String) -> Unit,
  modifier: Modifier = Modifier,
  viewModel: PasswordViewModel = hiltViewModel(),
  snackbarChannel: Channel<String> = LocalSnackbarChannel.current,
  context: Context = LocalContext.current
) {
  val state by viewModel.viewState.collectAsStateWithLifecycle()
  val loading by viewModel.isLoading.collectAsStateWithLifecycle()

  LaunchedEffect(viewModel.sideEffect) {
    viewModel.sideEffect.collectLatest {
      when (it) {
        is PasswordSideEffect.NavigateToLogin -> navigateToLogin()
        is PasswordSideEffect.NavigateToNickname -> navigateToNickname(it.email, it.password)
        is PasswordSideEffect.ShowSnackbar -> snackbarChannel.send(it.message.asString(context))
      }
    }
  }

  LaunchedEffect(Unit) { viewModel.sendEvent(PasswordEvent.ScreenInitialize) }

  if (loading) LoadingScreen()
  else PasswordScreen(
    state = state,
    modifier = modifier,
    onEvent = viewModel::sendEvent
  )
}

@Composable
private fun PasswordScreen(
  state: PasswordState,
  modifier: Modifier = Modifier,
  onEvent: (PasswordEvent) -> Unit = {}
) {
  Screen(
    modifier = modifier.padding(horizontal = 24.dp)
  ) {
    val formatError = state.password.isNotEmpty() && (state.password.length < 10 || state.password.length > 20)
    val confirmError = state.password.isNotEmpty() && state.passwordConfirm.isNotEmpty() && state.password != state.passwordConfirm

    Gap()
    Text(
      text = stringResource(R.string.password_title),
      modifier = Modifier.fillMaxWidth(),
      style = MaterialTheme.typography.titleLarge
    )
    Gap(height = 4.dp)
    Text(
      text = stringResource(R.string.password_email, state.email),
      modifier = Modifier.fillMaxWidth(),
      color = MaterialTheme.colorScheme.outline,
      style = MaterialTheme.typography.bodySmall
    )
    Gap(height = 32.dp)
    Text(
      text = stringResource(R.string.password_description),
      modifier = Modifier.fillMaxWidth()
    )
    Gap(height = 64.dp)
    Column(
      modifier = Modifier.fillMaxWidth(),
      verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
      Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        PasswordField(
          text = state.password,
          onTextChange = { onEvent(PasswordEvent.OnPasswordChange(it)) },
          hintText = stringResource(R.string.password_password_hint),
          modifier = Modifier.fillMaxWidth(),
          isError = if (state.password.isEmpty()) null else formatError,
          errorMessage = stringResource(R.string.password_password_format_error),
        )
        PasswordField(
          text = state.passwordConfirm,
          onTextChange = { onEvent(PasswordEvent.OnPasswordConfirmChange(it)) },
          hintText = stringResource(R.string.password_password_confirm_hint),
          modifier = Modifier.fillMaxWidth(),
          enabled = state.password.isNotEmpty(),
          isError = if (state.passwordConfirm.isEmpty()) null else confirmError,
          errorMessage = stringResource(R.string.password_password_confirm_error)
        )
      }
      Button(
        onClick = { onEvent(PasswordEvent.OnClickNextButton) },
        modifier = Modifier.fillMaxWidth(),
        enabled = state.password.isNotEmpty() && state.passwordConfirm.isNotEmpty() && !formatError && !confirmError
      ) {
        Text(text = stringResource(R.string.password_next_button))
      }
    }
    Gap()
    Gap()
  }
}

@Preview
@Composable
private fun Default() {
  ScreenPreview {
    PasswordScreen(
      PasswordState(
        email = "email@email.com"
      )
    )
  }
}

@Preview
@Composable
private fun PasswordError() {
  ScreenPreview {
    PasswordScreen(
      PasswordState(
        email = "email@email.com",
        password = "password"
      )
    )
  }
}

@Preview
@Composable
private fun PasswordConfirmError() {
  ScreenPreview {
    PasswordScreen(
      PasswordState(
        email = "email@email.com",
        password = "password123",
        passwordConfirm = "password124"
      )
    )
  }
}

@Preview
@Composable
private fun Valid() {
  ScreenPreview {
    PasswordScreen(
      PasswordState(
        email = "email@email.com",
        password = "password123",
        passwordConfirm = "password123"
      )
    )
  }
}