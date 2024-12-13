package com.jwd.lunchvote.presentation.screen.login.register.email_verification

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import com.jwd.lunchvote.presentation.screen.login.register.email_verification.EmailVerificationContract.EmailVerificationEvent
import com.jwd.lunchvote.presentation.screen.login.register.email_verification.EmailVerificationContract.EmailVerificationSideEffect
import com.jwd.lunchvote.presentation.screen.login.register.email_verification.EmailVerificationContract.EmailVerificationState
import com.jwd.lunchvote.presentation.util.LocalSnackbarChannel
import com.jwd.lunchvote.presentation.widget.Gap
import com.jwd.lunchvote.presentation.widget.LoadingScreen
import com.jwd.lunchvote.presentation.widget.Screen
import com.jwd.lunchvote.presentation.widget.ScreenPreview
import com.jwd.lunchvote.presentation.widget.TextField
import com.jwd.lunchvote.presentation.widget.TextFieldIconDefaults
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collectLatest
import kr.co.inbody.config.config.EmailConfig

@Composable
fun EmailVerificationRoute(
  modifier: Modifier = Modifier,
  viewModel: EmailVerificationViewModel = hiltViewModel(),
  snackbarChannel: Channel<String> = LocalSnackbarChannel.current,
  context: Context = LocalContext.current
) {
  val state by viewModel.viewState.collectAsStateWithLifecycle()
  val loading by viewModel.isLoading.collectAsStateWithLifecycle()

  LaunchedEffect(viewModel.sideEffect) {
    viewModel.sideEffect.collectLatest {
      when (it) {
        is EmailVerificationSideEffect.ShowSnackbar -> snackbarChannel.send(it.message.asString(context))
      }
    }
  }

  if (loading) LoadingScreen()
  else EmailVerificationScreen(
    state = state,
    modifier = modifier,
    onEvent = viewModel::sendEvent
  )
}

@Composable
private fun EmailVerificationScreen(
  state: EmailVerificationState,
  modifier: Modifier = Modifier,
  onEvent: (EmailVerificationEvent) -> Unit = {}
) {
  Screen(
    modifier = modifier.padding(horizontal = 24.dp)
  ) {
    Gap()
    Text(
      text = stringResource(R.string.email_verification_title),
      modifier = Modifier.fillMaxWidth(),
      style = MaterialTheme.typography.titleLarge
    )
    Gap(height = 32.dp)
    Text(
      text = stringResource(R.string.email_verification_description),
      modifier = Modifier.fillMaxWidth()
    )
    Gap(height = 64.dp)
    Column(
      modifier = Modifier.fillMaxWidth(),
      verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
      val isValid = EmailConfig.REGEX.matches(state.email)

      TextField(
        text = state.email,
        onTextChange = { onEvent(EmailVerificationEvent.OnEmailChange(it)) },
        hintText = stringResource(R.string.email_verification_email_hint),
        modifier = Modifier.fillMaxWidth(),
        enabled = state.emailSent.not(),
        isError = if (state.email.isEmpty()) null else state.email.isNotEmpty() && isValid.not(),
        errorMessage = stringResource(R.string.email_verification_email_format_error),
        trailingIcon = {
          if (state.emailSent) {
            TextFieldIconDefaults.CheckIcon()
          }
        }
      )
      if (state.emailSent.not()) {
        Button(
          onClick = { onEvent(EmailVerificationEvent.OnClickSendButton) },
          modifier = Modifier.fillMaxWidth(),
          enabled = state.email.isNotEmpty() && isValid
        ) {
          Text(text = stringResource(R.string.email_verification_send_button))
        }
      } else {
        OutlinedButton(
          onClick = { onEvent(EmailVerificationEvent.OnClickResendButton) },
          modifier = Modifier.fillMaxWidth()
        ) {
          Text(text = stringResource(R.string.email_verification_resend_button))
        }
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
    EmailVerificationScreen(
      EmailVerificationState()
    )
  }
}

@Preview
@Composable
private fun ValidEmail() {
  ScreenPreview {
    EmailVerificationScreen(
      EmailVerificationState(
        email = "email@email.com"
      )
    )
  }
}

@Preview
@Composable
private fun InvalidEmail() {
  ScreenPreview {
    EmailVerificationScreen(
      EmailVerificationState(
        email = "email"
      )
    )
  }
}

@Preview
@Composable
private fun EmailSent() {
  ScreenPreview {
    EmailVerificationScreen(
      EmailVerificationState(
        email = "email@email.com",
        emailSent = true
      )
    )
  }
}