package com.jwd.lunchvote.presentation.ui.login.password

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jwd.lunchvote.presentation.R
import com.jwd.lunchvote.presentation.ui.login.password.PasswordContract.PasswordEvent
import com.jwd.lunchvote.presentation.ui.login.password.PasswordContract.PasswordSideEffect
import com.jwd.lunchvote.presentation.ui.login.password.PasswordContract.PasswordState
import com.jwd.lunchvote.presentation.widget.LoadingScreen
import com.jwd.lunchvote.presentation.widget.LunchVoteTextField
import com.jwd.lunchvote.presentation.widget.Screen
import com.jwd.lunchvote.presentation.widget.ScreenPreview
import kotlinx.coroutines.flow.collectLatest

@Composable
fun PasswordRoute(
  navigateToNickname: () -> Unit,
  showSnackBar: suspend (String) -> Unit,
  modifier: Modifier = Modifier,
  viewModel: PasswordViewModel = hiltViewModel(),
  context: Context = LocalContext.current
) {
  val state by viewModel.viewState.collectAsStateWithLifecycle()
  val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()

  LaunchedEffect(viewModel.sideEffect) {
    viewModel.sideEffect.collectLatest {
      when (it) {
        is PasswordSideEffect.NavigateToNickname -> navigateToNickname()
        is PasswordSideEffect.ShowSnackBar -> showSnackBar(it.message.asString(context))
      }
    }
  }

  if (isLoading) LoadingScreen()
  else PasswordScreen(
    state = state,
    modifier = modifier,
    onPasswordChanged = { viewModel.sendEvent(PasswordEvent.OnPasswordChanged(it)) },
    onPasswordConfirmChanged = { viewModel.sendEvent(PasswordEvent.OnPasswordConfirmChanged(it)) },
    onClickNextButton = { viewModel.sendEvent(PasswordEvent.OnClickNextButton) }
  )
}

@Composable
private fun PasswordScreen(
  state: PasswordState,
  modifier: Modifier = Modifier,
  onPasswordChanged: (String) -> Unit = {},
  onPasswordConfirmChanged: (String) -> Unit = {},
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

      val formatError = state.password.isNotEmpty() && (state.password.length < 10 || state.password.length > 20)
      val confirmError = state.password.isNotEmpty() && state.passwordConfirm.isNotEmpty() && state.password != state.passwordConfirm

      Text(
        text = stringResource(R.string.password_title),
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 24.dp)
          .constrainAs(title) {
            bottom.linkTo(description.top, 32.dp)
          },
        style = MaterialTheme.typography.titleLarge
      )
      Text(
        text = stringResource(R.string.password_description),
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
        Column(
          modifier = Modifier.fillMaxWidth(),
          verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
          LunchVoteTextField(
            text = state.password,
            onTextChange = onPasswordChanged,
            hintText = stringResource(R.string.password_password_hint),
            modifier = Modifier.fillMaxWidth(),
            isError = formatError,
            visualTransformation = PasswordVisualTransformation()
          )
          LunchVoteTextField(
            text = state.passwordConfirm,
            onTextChange = onPasswordConfirmChanged,
            hintText = stringResource(R.string.password_password_confirm_hint),
            modifier = Modifier.fillMaxWidth(),
            isError = confirmError,
            visualTransformation = PasswordVisualTransformation()
          )
          Text(
            text = if (confirmError) stringResource(R.string.password_password_confirm_error) else stringResource(R.string.password_password_format_error),
            modifier = Modifier
              .padding(horizontal = 8.dp)
              .alpha(if (confirmError || formatError) 1f else 0f),
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.labelMedium
          )
        }
      }
      Button(
        onClick = onClickNextButton,
        modifier = Modifier
          .constrainAs(nextButton) {
            bottom.linkTo(parent.bottom, 64.dp)
            start.linkTo(parent.start)
            end.linkTo(parent.end)
          },
        enabled = state.password.isNotEmpty() && state.passwordConfirm.isNotEmpty() && !formatError && !confirmError
      ) {
        Text(text = stringResource(R.string.password_next_button))
      }
    }
  }
}

@Preview
@Composable
private fun Preview1() {
  ScreenPreview {
    PasswordScreen(
      PasswordState()
    )
  }
}

@Preview
@Composable
private fun Preview2() {
  ScreenPreview {
    PasswordScreen(
      PasswordState(
        password = "password"
      )
    )
  }
}

@Preview
@Composable
private fun Preview3() {
  ScreenPreview {
    PasswordScreen(
      PasswordState(
        password = "password123",
        passwordConfirm = "password124"
      )
    )
  }
}

@Preview
@Composable
private fun Preview4() {
  ScreenPreview {
    PasswordScreen(
      PasswordState(
        password = "password123",
        passwordConfirm = "password123"
      )
    )
  }
}