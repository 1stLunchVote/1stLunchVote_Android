package com.jwd.lunchvote.presentation.ui.login.register.nickname

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
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jwd.lunchvote.presentation.R
import com.jwd.lunchvote.presentation.ui.login.register.nickname.NicknameContract.NicknameEvent
import com.jwd.lunchvote.presentation.ui.login.register.nickname.NicknameContract.NicknameSideEffect
import com.jwd.lunchvote.presentation.ui.login.register.nickname.NicknameContract.NicknameState
import com.jwd.lunchvote.presentation.widget.LoadingScreen
import com.jwd.lunchvote.presentation.widget.LunchVoteTextField
import com.jwd.lunchvote.presentation.widget.Screen
import com.jwd.lunchvote.presentation.widget.ScreenPreview
import kotlinx.coroutines.flow.collectLatest

@Composable
fun NicknameRoute(
  navigateToHome: () -> Unit,
  showSnackBar: suspend (String) -> Unit,
  modifier: Modifier = Modifier,
  viewModel: NicknameViewModel = hiltViewModel(),
  context: Context = LocalContext.current
) {
  val state by viewModel.viewState.collectAsStateWithLifecycle()
  val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()

  LaunchedEffect(viewModel.sideEffect) {
    viewModel.sideEffect.collectLatest {
      when (it) {
        is NicknameSideEffect.NavigateToHome -> navigateToHome()
        is NicknameSideEffect.ShowSnackBar -> showSnackBar(it.message.asString(context))
      }
    }
  }

  if (isLoading) LoadingScreen()
  else NicknameScreen(
    state = state,
    modifier = modifier,
    onNicknameChanged = { viewModel.sendEvent(NicknameEvent.OnNicknameChanged(it)) },
    onClickNextButton = { viewModel.sendEvent(NicknameEvent.OnClickNextButton) }
  )
}

@Composable
private fun NicknameScreen(
  state: NicknameState,
  modifier: Modifier = Modifier,
  onNicknameChanged: (String) -> Unit = {},
  onClickNextButton: () -> Unit = {}
) {
  Screen(
    modifier = modifier,
    scrollable = false
  ) {
    ConstraintLayout(
      modifier = Modifier.fillMaxSize()
    ) {
      val (title, description, input, nextButton) = createRefs()

      Text(
        text = stringResource(R.string.nickname_title),
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 24.dp)
          .constrainAs(title) {
            bottom.linkTo(description.top, 32.dp)
          },
        style = MaterialTheme.typography.titleLarge
      )
      Text(
        text = stringResource(R.string.nickname_description),
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 24.dp)
          .constrainAs(description) {
            bottom.linkTo(input.top, 64.dp)
          },
        style = MaterialTheme.typography.bodyLarge
      )
      LunchVoteTextField(
        text = state.nickname,
        onTextChange = onNicknameChanged,
        hintText = stringResource(R.string.nickname_nickname_hint),
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 24.dp)
          .constrainAs(input) {
            top.linkTo(parent.top)
            bottom.linkTo(parent.bottom)
          }
      )
      Button(
        onClick = onClickNextButton,
        modifier = Modifier
          .constrainAs(nextButton) {
            bottom.linkTo(parent.bottom, 64.dp)
            start.linkTo(parent.start)
            end.linkTo(parent.end)
          },
        enabled = state.nickname.isNotEmpty()
      ) {
        Text(text = stringResource(R.string.nickname_next_button))
      }
    }
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