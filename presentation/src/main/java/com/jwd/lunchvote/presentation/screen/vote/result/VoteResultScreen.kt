package com.jwd.lunchvote.presentation.screen.vote.result

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jwd.lunchvote.presentation.R
import com.jwd.lunchvote.presentation.model.FoodUIModel
import com.jwd.lunchvote.presentation.screen.vote.result.VoteResultContract.VoteResultEvent
import com.jwd.lunchvote.presentation.screen.vote.result.VoteResultContract.VoteResultSideEffect
import com.jwd.lunchvote.presentation.screen.vote.result.VoteResultContract.VoteResultState
import com.jwd.lunchvote.presentation.util.LocalSnackbarChannel
import com.jwd.lunchvote.presentation.widget.FAB
import com.jwd.lunchvote.presentation.widget.Gap
import com.jwd.lunchvote.presentation.widget.ImageFromUri
import com.jwd.lunchvote.presentation.widget.LoadingScreen
import com.jwd.lunchvote.presentation.widget.Screen
import com.jwd.lunchvote.presentation.widget.ScreenPreview
import com.jwd.lunchvote.presentation.widget.TopBar
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collectLatest

@Composable
fun VoteResultRoute(
  navigateToHome: () -> Unit,
  modifier: Modifier = Modifier,
  viewModel: VoteResultViewModel = hiltViewModel(),
  snackbarChannel: Channel<String> = LocalSnackbarChannel.current,
  context: Context = LocalContext.current
) {
  val state by viewModel.viewState.collectAsStateWithLifecycle()
  val loading by viewModel.isLoading.collectAsStateWithLifecycle()

  LaunchedEffect(Unit) {
    viewModel.sideEffect.collectLatest {
       when (it) {
        is VoteResultSideEffect.NavigateToHome -> navigateToHome()
        is VoteResultSideEffect.ShowSnackbar -> snackbarChannel.send(it.message.asString(context))
      }
    }
  }

  LaunchedEffect(Unit) { viewModel.sendEvent(VoteResultEvent.ScreenInitialize) }

  if (loading) LoadingScreen(
    message = stringResource(R.string.vote_result_loading)
  ) else VoteResultScreen(
    state = state,
    modifier = modifier,
    onEvent = viewModel::sendEvent
  )
}

@Composable
private fun VoteResultScreen(
  state: VoteResultState,
  modifier: Modifier = Modifier,
  onEvent: (VoteResultEvent) -> Unit = {}
) {
  Screen(
    modifier = modifier.fillMaxSize(),
    topAppBar = {
      TopBar(
        title = stringResource(R.string.vote_result_title),
        navIconVisible = false
      )
    },
    actions = {
      if (state.coverAlpha < 0.32f) {
        FAB(
          text = stringResource(R.string.vote_result_home_button),
          onClick = { onEvent(VoteResultEvent.OnClickHomeButton) }
        )
      }
    }
  ) {
    Gap()
    Text(
      text = stringResource(R.string.vote_result_header)
    )
    Gap(height = 16.dp)
    Box(
      modifier = Modifier.size(256.dp),
      contentAlignment = Alignment.Center
    ) {
      Column(
        modifier = Modifier.matchParentSize(),
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        VoteResultImage(
          foodImageUri = state.food.imageUrl,
          voteRatio = state.voteRatio,
          modifier = Modifier.size(156.dp)
        )
        Text(
          text = state.food.name,
          style = MaterialTheme.typography.headlineSmall
        )
      }
      VoteResultCover(
        coverAlpha = state.coverAlpha,
        modifier = Modifier
          .matchParentSize()
          .align(Alignment.TopCenter),
        onPressRevealBox = { onEvent(VoteResultEvent.OnPressRevealBox) }
      )
    }
    Gap()
    Gap()
  }
}

@Composable
private fun VoteResultCover(
  coverAlpha: Float,
  modifier: Modifier = Modifier,
  onPressRevealBox: () -> Unit = {}
) {
  Box(
    modifier = modifier
      .clip(MaterialTheme.shapes.medium)
      .pointerInput(Unit) {
        detectDragGestures { _, dragAmount ->
          if (dragAmount.y > 50 || dragAmount.x > 50) onPressRevealBox()
        }
      }
      .alpha(coverAlpha)
      .background(MaterialTheme.colorScheme.outlineVariant),
    contentAlignment = Alignment.Center
  ) {
    Text(
      text = stringResource(R.string.vote_result_cover),
      modifier = Modifier.alpha(0.32f),
      textAlign = TextAlign.Center,
      style = MaterialTheme.typography.titleMedium
    )
  }
}

@Composable
private fun VoteResultImage(
  foodImageUri: String,
  voteRatio: Float,
  modifier: Modifier = Modifier
) {
  Box(
    modifier = modifier,
    contentAlignment = Alignment.Center
  ) {
    ImageFromUri(
      uri = foodImageUri.toUri(),
      modifier = Modifier.size(156.dp)
    )
    Box(
      modifier = modifier.offset(x = 72.dp, y = 72.dp),
      contentAlignment = Alignment.Center
    ) {
      Image(
        painter = painterResource(R.drawable.img_vote_result_ratio),
        contentDescription = "food ratio badge",
        modifier = Modifier.size(92.dp)
      )
      Text(
        text = stringResource(R.string.vote_result_badge, (voteRatio * 100).toInt()),
        color = MaterialTheme.colorScheme.background,
        textAlign = TextAlign.Center,
        style = MaterialTheme.typography.titleMedium
      )
    }
  }
}

@Preview
@Composable
private fun Default() {
  ScreenPreview {
    VoteResultScreen(
      VoteResultState(
        food = FoodUIModel(
          name = "햄버거"
        ),
        voteRatio = 0.7f
      )
    )
  }
}

@Preview
@Composable
private fun CoverRevealed() {
  ScreenPreview {
    VoteResultScreen(
      VoteResultState(
        food = FoodUIModel(
          name = "햄버거"
        ),
        voteRatio = 0.7f,
        coverAlpha = 0f
      )
    )
  }
}