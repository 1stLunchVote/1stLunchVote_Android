package com.jwd.lunchvote.presentation.screen.login.register.nickname

import android.content.Context
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jwd.lunchvote.presentation.R
import com.jwd.lunchvote.presentation.screen.login.register.nickname.NicknameContract.NicknameEvent
import com.jwd.lunchvote.presentation.screen.login.register.nickname.NicknameContract.NicknameSideEffect
import com.jwd.lunchvote.presentation.screen.login.register.nickname.NicknameContract.NicknameState
import com.jwd.lunchvote.presentation.util.LocalSnackbarChannel
import com.jwd.lunchvote.presentation.widget.Gap
import com.jwd.lunchvote.presentation.widget.LoadingScreen
import com.jwd.lunchvote.presentation.widget.LunchVoteTextField
import com.jwd.lunchvote.presentation.widget.Screen
import com.jwd.lunchvote.presentation.widget.ScreenPreview
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collectLatest

@Composable
fun NicknameRoute(
  navigateToHome: () -> Unit,
  modifier: Modifier = Modifier,
  viewModel: NicknameViewModel = hiltViewModel(),
  snackbarChannel: Channel<String> = LocalSnackbarChannel.current,
  context: Context = LocalContext.current
) {
  val state by viewModel.viewState.collectAsStateWithLifecycle()
  val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()

  LaunchedEffect(viewModel.sideEffect) {
    viewModel.sideEffect.collectLatest {
      when (it) {
        is NicknameSideEffect.NavigateToHome -> navigateToHome()
        is NicknameSideEffect.ShowSnackbar -> snackbarChannel.send(it.message.asString(context))
      }
    }
  }

  if (isLoading) LoadingScreen()
  else NicknameScreen(
    state = state,
    modifier = modifier,
    onEvent = viewModel::sendEvent
  )
}

@Composable
private fun NicknameScreen(
  state: NicknameState,
  modifier: Modifier = Modifier,
  onEvent: (NicknameEvent) -> Unit = {}
) {
  Screen(
    modifier = modifier
      .fillMaxSize()
      .padding(horizontal = 24.dp)
  ) {
    Gap()
    Text(
      text = stringResource(R.string.nickname_title),
      modifier = Modifier.fillMaxWidth(),
      style = MaterialTheme.typography.titleLarge
    )
    Gap(height = 32.dp)
    Text(
      text = stringResource(R.string.nickname_description),
      modifier = Modifier.fillMaxWidth(),
      style = MaterialTheme.typography.bodyLarge
    )
    Gap(height = 64.dp)
    LunchVoteTextField(
      text = state.nickname,
      onTextChange = { onEvent(NicknameEvent.OnNicknameChange(it)) },
      hintText = stringResource(R.string.nickname_nickname_hint),
      modifier = Modifier.fillMaxWidth()
    )
    Gap(height = 20.dp)
    Button(
      onClick = { onEvent(NicknameEvent.OnClickNextButton) },
      modifier = Modifier.fillMaxWidth(),
      enabled = state.nickname.isNotEmpty()
    ) {
      Text(text = stringResource(R.string.nickname_next_button))
    }
    Gap()
    Gap()
  }
}

@Preview
@Composable
private fun Preview() {
  ScreenPreview {
    NicknameScreen(
      NicknameState(
        nickname = "닉네임"
      )
    )
  }
}