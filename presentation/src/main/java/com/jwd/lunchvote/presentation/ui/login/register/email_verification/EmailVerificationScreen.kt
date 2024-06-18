package com.jwd.lunchvote.presentation.ui.login.register.email_verification

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jwd.lunchvote.presentation.R
import com.jwd.lunchvote.presentation.ui.login.register.email_verification.EmailVerificationContract.EmailVerificationEvent
import com.jwd.lunchvote.presentation.ui.login.register.email_verification.EmailVerificationContract.EmailVerificationSideEffect
import com.jwd.lunchvote.presentation.ui.login.register.email_verification.EmailVerificationContract.EmailVerificationState
import com.jwd.lunchvote.presentation.widget.LunchVoteTextField
import com.jwd.lunchvote.presentation.widget.Screen
import com.jwd.lunchvote.presentation.widget.ScreenPreview
import kotlinx.coroutines.flow.collectLatest
import kr.co.inbody.config.config.EmailConfig

@Composable
fun EmailVerificationRoute(
  navigateToPassword: () -> Unit,
  showSnackBar: suspend (String) -> Unit,
  modifier: Modifier = Modifier,
  viewModel: EmailVerificationViewModel = hiltViewModel(),
  context: Context = LocalContext.current
) {
  val state by viewModel.viewState.collectAsStateWithLifecycle()

  LaunchedEffect(viewModel.sideEffect) {
    viewModel.sideEffect.collectLatest {
      when (it) {
        is EmailVerificationSideEffect.NavigateToPassword -> navigateToPassword()
        is EmailVerificationSideEffect.ShowSnackBar -> showSnackBar(it.message.asString(context))
      }
    }
  }

  EmailVerificationScreen(
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
    modifier = modifier,
    scrollable = false
  ) {
    ConstraintLayout(
      modifier = Modifier.fillMaxSize()
    ) {
      val (title, description, inputColumn) = createRefs()

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
        verticalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        val isValid = EmailConfig.REGEX.matches(state.email)

        Column(
          modifier = Modifier.fillMaxWidth(),
          verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
          LunchVoteTextField(
            text = state.email,
            onTextChange = { onEvent(EmailVerificationEvent.OnEmailChange(it)) },
            hintText = stringResource(R.string.email_verification_email_hint),
            modifier = Modifier.fillMaxWidth(),
            enabled = state.emailSent.not(),
            isError = state.email.isNotEmpty() && isValid.not()
          )
          Text(
            text = stringResource(R.string.email_verification_email_format_error),
            modifier = Modifier
              .padding(horizontal = 8.dp)
              .alpha(if (state.email.isNotEmpty() && isValid.not()) 1f else 0f),
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.labelMedium
          )
        }
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
        emailSent = true
      )
    )
  }
}