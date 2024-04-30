package com.jwd.lunchvote.presentation.ui.login.email_verification

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jwd.lunchvote.presentation.R
import com.jwd.lunchvote.presentation.ui.login.email_verification.EmailVerificationContract.EmailVerificationEvent
import com.jwd.lunchvote.presentation.ui.login.email_verification.EmailVerificationContract.EmailVerificationSideEffect
import com.jwd.lunchvote.presentation.ui.login.email_verification.EmailVerificationContract.EmailVerificationState
import com.jwd.lunchvote.presentation.widget.LoadingScreen
import com.jwd.lunchvote.presentation.widget.LunchVoteTextField
import com.jwd.lunchvote.presentation.widget.Screen
import com.jwd.lunchvote.presentation.widget.ScreenPreview
import kotlinx.coroutines.flow.collectLatest

@Composable
fun EmailVerificationRoute(
  popBackStack: () -> Unit,
  navigateToPassword: () -> Unit,
  showSnackBar: suspend (String) -> Unit,
  modifier: Modifier = Modifier,
  viewModel: EmailVerificationViewModel = hiltViewModel(),
  context: Context = LocalContext.current
) {
  val state by viewModel.viewState.collectAsStateWithLifecycle()
  val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
  val dialogState by viewModel.dialogState.collectAsStateWithLifecycle()

  LaunchedEffect(viewModel.sideEffect) {
    viewModel.sideEffect.collectLatest {
      when (it) {
        is EmailVerificationSideEffect.PopBackStack -> popBackStack()
        is EmailVerificationSideEffect.OpenQuitDialog -> viewModel.setDialogState(EmailVerificationContract.QUIT_DIALOG)
        is EmailVerificationSideEffect.NavigateToPassword -> navigateToPassword()
        is EmailVerificationSideEffect.ShowSnackBar -> showSnackBar(it.message.asString(context))
      }
    }
  }

  when(dialogState) {
    EmailVerificationContract.QUIT_DIALOG -> {
      // TODO: Implement dialog
    }
  }

  if (isLoading) LoadingScreen()
  else EmailVerificationScreen(
    state = state,
    modifier = modifier,
    onEmailChanged = { viewModel.sendEvent(EmailVerificationEvent.OnEmailChanged(it)) },
    onClickSendButton = { viewModel.sendEvent(EmailVerificationEvent.OnClickSendButton) },
    onClickResendButton = { viewModel.sendEvent(EmailVerificationEvent.OnClickResendButton) },
    onCodeChanged = { viewModel.sendEvent(EmailVerificationEvent.OnCodeChanged(it)) },
    onClickNextButton = { viewModel.sendEvent(EmailVerificationEvent.OnClickNextButton) }
  )
}

@Composable
private fun EmailVerificationScreen(
  state: EmailVerificationState,
  modifier: Modifier = Modifier,
  onEmailChanged: (String) -> Unit = {},
  onClickSendButton: () -> Unit = {},
  onClickResendButton: () -> Unit = {},
  onCodeChanged: (String) -> Unit = {},
  onClickNextButton: () -> Unit = {}
) {
  Screen(
    modifier = modifier,
    scrollable = false
  ) {
    ConstraintLayout(
      modifier = Modifier.fillMaxSize()
    ) {
      val (title, description, inputColumn, nextButton) = createRefs()

      Text(
        text = stringResource(R.string.email_verification_title),
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 24.dp)
          .constrainAs(title) {
            bottom.linkTo(description.top, 32.dp)
          },
        style = MaterialTheme.typography.titleLarge
      )
      Text(
        text = stringResource(R.string.email_verification_description),
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 24.dp)
          .constrainAs(description) {
            bottom.linkTo(inputColumn.top, 64.dp)
          },
        style = MaterialTheme.typography.bodyLarge
      )
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 24.dp)
          .constrainAs(inputColumn) {
            top.linkTo(parent.top)
            bottom.linkTo(parent.bottom)
          },
        verticalArrangement = Arrangement.spacedBy(16.dp)
      ) {
        val regex = Regex("^([0-9a-zA-Z_\\.-]+)@([0-9a-zA-Z_-]+)(\\.[0-9a-zA-Z_-]+){1,2}$")

        Column(
          modifier = Modifier.fillMaxWidth(),
          verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
          LunchVoteTextField(
            text = state.email,
            onTextChange = onEmailChanged,
            hintText = stringResource(R.string.email_verification_email_hint),
            modifier = Modifier.fillMaxWidth(),
            enabled = state.emailSent.not()
          )
          Text(
            text = stringResource(R.string.email_verification_email_format_error),
            modifier = Modifier
              .padding(horizontal = 8.dp)
              .alpha(if (regex.matches(state.email) || state.email.isEmpty()) 0f else 1f),
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.labelMedium
          )
        }
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          if (state.emailSent.not()) {
            Button(
              onClick = onClickSendButton,
              modifier = Modifier.weight(1f),
              enabled = state.email.isNotEmpty() && regex.matches(state.email)
            ) {
              Text(text = stringResource(R.string.email_verification_send_button))
            }
          } else {
            OutlinedButton(
              onClick = onClickResendButton,
              modifier = Modifier.weight(1f)
            ) {
              Text(text = stringResource(R.string.email_verification_resend_button))
            }
          }
          LunchVoteTextField(
            text = state.code,
            onTextChange = onCodeChanged,
            hintText = stringResource(R.string.email_verification_code_hint),
            modifier = Modifier
              .weight(2f)
              .alpha(if (state.emailSent) 1f else 0f),
            enabled = state.emailSent
          )
        }
      }
      if (state.emailSent) {
        Button(
          onClick = onClickNextButton,
          modifier = Modifier
            .width(120.dp)
            .constrainAs(nextButton) {
              bottom.linkTo(parent.bottom, 64.dp)
              start.linkTo(parent.start)
              end.linkTo(parent.end)
            },
          enabled = state.code.length == 6
        ) {
          Text(text = stringResource(R.string.email_verification_next_button))
        }
      }
    }
  }
}

@Preview
@Composable
private fun Preview1() {
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
private fun Preview2() {
  ScreenPreview {
    EmailVerificationScreen(
      EmailVerificationState(
        email = "email@email.com",
        emailSent = true,
        code = "123456"
      )
    )
  }
}