package com.jwd.lunchvote.presentation.ui.vote.result

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jwd.lunchvote.presentation.R
import com.jwd.lunchvote.presentation.model.FoodUIModel
import com.jwd.lunchvote.presentation.ui.vote.result.VoteResultContract.VoteResultEvent
import com.jwd.lunchvote.presentation.ui.vote.result.VoteResultContract.VoteResultSideEffect
import com.jwd.lunchvote.presentation.ui.vote.result.VoteResultContract.VoteResultState
import com.jwd.lunchvote.presentation.widget.Gap
import com.jwd.lunchvote.presentation.widget.LoadingScreen
import com.jwd.lunchvote.presentation.widget.LunchVoteTopBar
import com.jwd.lunchvote.presentation.widget.Screen
import com.jwd.lunchvote.presentation.widget.ScreenPreview
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.coil.CoilImage
import kotlinx.coroutines.flow.collectLatest

@Composable
fun VoteResultRoute(
  navigateToHome: () -> Unit,
  showSnackBar: suspend (String) -> Unit,
  modifier: Modifier = Modifier,
  viewModel: VoteResultViewModel = hiltViewModel(),
  context: Context = LocalContext.current
) {
  val state by viewModel.viewState.collectAsStateWithLifecycle()
  val loading by viewModel.isLoading.collectAsStateWithLifecycle()

  LaunchedEffect(Unit) {
    viewModel.sideEffect.collectLatest {
       when (it) {
        is VoteResultSideEffect.NavigateToHome -> navigateToHome()
        is VoteResultSideEffect.ShowSnackBar -> showSnackBar(it.message.asString(context))
      }
    }
  }

  if (loading) LoadingScreen(
    message = "투표 결과를 집계중입니다..."
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
    modifier = modifier,
    topAppBar = {
      LunchVoteTopBar(
        title = "투표 결과",
        navIconVisible = false
      )
    },
    scrollable = false
  ) {
    Column(
      modifier = Modifier.fillMaxSize(),
      verticalArrangement = Arrangement.Center,
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Text(
        text = "오늘의 메뉴는...",
        style = MaterialTheme.typography.bodyLarge
      )
      Gap(height = 8.dp)
      VoteResultImage(
        food = state.food,
        voteRatio = state.voteRatio,
        modifier = Modifier.size(156.dp)
      )
      Gap(height = 48.dp)
      Text(
        text = state.food.name,
        style = MaterialTheme.typography.headlineSmall
      )
      Gap(height = 80.dp)
      Button(
        onClick = { onEvent(VoteResultEvent.OnClickHomeButton) }
      ) {
        Text(
          text = "메인 화면으로 돌아가기"
        )
      }
    }
  }
}

@Composable
private fun VoteResultImage(
  food: FoodUIModel,
  voteRatio: Float,
  modifier: Modifier = Modifier
) {
  Box(
    modifier = modifier,
    contentAlignment = Alignment.Center
  ) {
    CoilImage(
      imageModel = { food.image },
      modifier = Modifier.size(156.dp),
      imageOptions = ImageOptions(
        contentScale = ContentScale.Crop
      ),
      previewPlaceholder = R.drawable.ic_food_image_temp
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
        text = "${(voteRatio * 100).toInt()}%의\n선택!",
        color = MaterialTheme.colorScheme.background,
        textAlign = TextAlign.Center,
        style = MaterialTheme.typography.titleMedium
      )
    }
  }
}

@Preview
@Composable
private fun Preview() {
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
